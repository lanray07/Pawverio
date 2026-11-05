param(
    [switch]$Initialize,
    [switch]$Candidate,
    [string]$KeyDirectory = (Join-Path $env:USERPROFILE '.codex/signing/pawverio-production'),
    [string]$SdkDirectory = (Join-Path $env:LOCALAPPDATA 'Android/Sdk')
)
$ErrorActionPreference = 'Stop'
$repo = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
$keyDir = [IO.Path]::GetFullPath($KeyDirectory)
if ($keyDir.StartsWith($repo + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase) -or $keyDir -eq $repo) {
    throw 'Private signing material must remain outside the repository.'
}
$store = Join-Path $keyDir 'Pawverio-production.p12'
$passwordFile = Join-Path $keyDir 'keystore-password.dpapi'
$alias = 'pawverio-release'
if (!(Test-Path -LiteralPath $store) -and !$Initialize) { throw 'No Pawverio key exists. Use -Initialize once to create a dedicated key.' }
if ((Test-Path -LiteralPath $store) -and !(Test-Path -LiteralPath $passwordFile)) { throw 'Existing keystore password is unavailable. Recover it; do not replace the key.' }
if (!(Test-Path -LiteralPath $keyDir)) { New-Item -ItemType Directory -Path $keyDir | Out-Null }
if ($Initialize) {
    $acl = Get-Acl -LiteralPath $keyDir
    $acl.SetAccessRuleProtection($true, $false)
    $rights = [Security.AccessControl.FileSystemRights]::FullControl
    $inherit = [Security.AccessControl.InheritanceFlags]::ContainerInherit -bor [Security.AccessControl.InheritanceFlags]::ObjectInherit
    foreach ($sid in @([Security.Principal.WindowsIdentity]::GetCurrent().User, [Security.Principal.SecurityIdentifier]::new('S-1-5-18'))) {
        $acl.AddAccessRule([Security.AccessControl.FileSystemAccessRule]::new($sid, $rights, $inherit, [Security.AccessControl.PropagationFlags]::None, [Security.AccessControl.AccessControlType]::Allow))
    }
    Set-Acl -LiteralPath $keyDir -AclObject $acl
}
if (!(Test-Path -LiteralPath $passwordFile)) {
    $random = New-Object byte[] 32
    [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($random)
    $securePassword = ConvertTo-SecureString ([BitConverter]::ToString($random).Replace('-', '')) -AsPlainText -Force
    $securePassword | ConvertFrom-SecureString | Set-Content -LiteralPath $passwordFile -Encoding Ascii
    [Array]::Clear($random, 0, $random.Length)
} else { $securePassword = (Get-Content -LiteralPath $passwordFile -Raw).Trim() | ConvertTo-SecureString }
$pointer = [IntPtr]::Zero
try {
    $pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    $env:PAWVERIO_KEYSTORE_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer)
    if (!(Test-Path -LiteralPath $store)) {
        & keytool -genkeypair -noprompt -keystore $store -storetype PKCS12 -alias $alias -keyalg RSA -keysize 4096 -sigalg SHA256withRSA -validity 10000 -dname 'CN=Pawverio' -storepass:env PAWVERIO_KEYSTORE_PASSWORD -keypass:env PAWVERIO_KEYSTORE_PASSWORD
        if ($LASTEXITCODE -ne 0) { throw 'Pawverio key creation failed.' }
    }
    $unsigned = Join-Path $repo 'app/build/outputs/apk/release/app-release-unsigned.apk'
    if (!(Test-Path -LiteralPath $unsigned)) { throw 'Build the release variant first.' }
    $tools = Join-Path $SdkDirectory 'build-tools/35.0.0'
    $aligned = Join-Path $repo 'app/build/outputs/apk/release/app-release-aligned.apk'
    $output = if ($Candidate) { Join-Path $repo 'app/build/outputs/apk/release' } else { Join-Path $repo 'deliverables/android/release' }
    $signed = Join-Path $output 'Pawverio-release-signed.apk'
    $logs = if ($Candidate) { $output } else { Join-Path $repo 'deliverables/android/build-logs' }
    $reports = if ($Candidate) { $output } else { Join-Path $repo 'deliverables/reports' }
    New-Item -ItemType Directory -Force -Path $logs | Out-Null
    & (Join-Path $tools 'zipalign.exe') -P 16 -f 4 $unsigned $aligned
    if ($LASTEXITCODE -ne 0) { throw 'APK alignment failed.' }
    & (Join-Path $tools 'apksigner.bat') sign --ks $store --ks-type PKCS12 --ks-key-alias $alias --ks-pass env:PAWVERIO_KEYSTORE_PASSWORD --key-pass env:PAWVERIO_KEYSTORE_PASSWORD --v1-signing-enabled true --v2-signing-enabled true --v3-signing-enabled true --v4-signing-enabled false --out $signed $aligned
    if ($LASTEXITCODE -ne 0) { throw 'APK signing failed.' }
    & (Join-Path $tools 'apksigner.bat') verify --verbose --print-certs --min-sdk-version 26 $signed | Tee-Object -FilePath (Join-Path $logs 'release-signature-verification.log')
    if ($LASTEXITCODE -ne 0) { throw 'APK signature verification failed.' }
    & (Join-Path $tools 'zipalign.exe') -c -P 16 4 $signed
    if ($LASTEXITCODE -ne 0) { throw 'Signed APK alignment verification failed.' }
    & python (Join-Path $repo 'scripts/audit_native_alignment.py') --apk $signed --output (Join-Path $reports 'NATIVE_PAGE_ALIGNMENT.json')
    if ($LASTEXITCODE -ne 0) { throw 'Packaged native ELF alignment verification failed.' }
    $summary = [ordered]@{status='PASS'; artifact='deliverables/android/release/Pawverio-release-signed.apk'; sha256=(Get-FileHash -LiteralPath $signed -Algorithm SHA256).Hash.ToLowerInvariant(); unsigned_sha256=(Get-FileHash -LiteralPath $unsigned -Algorithm SHA256).Hash.ToLowerInvariant(); alias=$alias; key_algorithm='RSA4096'; signature='SHA256withRSA'; minimum_verified_api=26; alignment_bytes=4; zip_entry_native_page_alignment_kib=16; device_runtime_verification='NOT TESTED: current Fire uses a different debug certificate; data was preserved'; password_storage='Windows CurrentUser DPAPI, outside repository'}
    if ($Candidate) {
        $summary.artifact = 'app/build/outputs/apk/release/Pawverio-release-signed.apk'
        $summary.device_runtime_verification = 'NOT TESTED: signed candidate has not been installed'
    }
    $summary | ConvertTo-Json | Set-Content -LiteralPath (Join-Path $reports 'SIGNED_RELEASE.json') -Encoding UTF8
    Write-Output ('Signed release verified: ' + $signed)
} finally {
    Remove-Item Env:PAWVERIO_KEYSTORE_PASSWORD -ErrorAction SilentlyContinue
    if ($pointer -ne [IntPtr]::Zero) { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer) }
    if ($securePassword) { $securePassword.Dispose() }
}
