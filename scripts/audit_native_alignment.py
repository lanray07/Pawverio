"""Check packaged ELF LOAD alignment without extracting or executing native libraries.

This is a static format check, not a 16 KiB device-runtime certification.
"""
import argparse
import hashlib
import json
import struct
from pathlib import Path
from zipfile import ZipFile


def inspect_elf(data):
    if data[:4] != b'\x7fELF' or data[4] not in (1, 2) or data[5] not in (1, 2):
        raise ValueError('Invalid ELF identification')
    endian = '<' if data[5] == 1 else '>'
    bits = 32 if data[4] == 1 else 64
    if bits == 64:
        phoff = struct.unpack_from(endian + 'Q', data, 32)[0]
        entsize, count = struct.unpack_from(endian + 'HH', data, 54)
        minimum = 56
    else:
        phoff = struct.unpack_from(endian + 'I', data, 28)[0]
        entsize, count = struct.unpack_from(endian + 'HH', data, 42)
        minimum = 32
    if entsize < minimum or count == 0 or phoff + entsize * count > len(data):
        raise ValueError('Invalid ELF program header bounds')
    loads = []
    for index in range(count):
        position = phoff + entsize * index
        kind = struct.unpack_from(endian + 'I', data, position)[0]
        if kind != 1:  # PT_LOAD
            continue
        if bits == 64:
            offset, virtual = struct.unpack_from(endian + 'QQ', data, position + 8)
            alignment = struct.unpack_from(endian + 'Q', data, position + 48)[0]
        else:
            offset, virtual = struct.unpack_from(endian + 'II', data, position + 4)
            alignment = struct.unpack_from(endian + 'I', data, position + 28)[0]
        loads.append({'file_offset': offset, 'virtual_address': virtual, 'alignment': alignment,
                      'supports_16k_alignment': alignment >= 16384 and alignment & (alignment - 1) == 0
                      and (virtual - offset) % 16384 == 0})
    if not loads:
        raise ValueError('ELF has no loadable segments')
    return {'bits': bits, 'load_segments': loads,
            'static_16k_alignment': all(x['supports_16k_alignment'] for x in loads)}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--apk', default='deliverables/android/release/Pawverio-release-signed.apk')
    parser.add_argument('--output', default='deliverables/reports/NATIVE_PAGE_ALIGNMENT.json')
    args = parser.parse_args()
    apk = Path(args.apk)
    libraries = []
    with ZipFile(apk) as archive:
        for name in archive.namelist():
            if name.startswith('lib/') and name.endswith('.so'):
                libraries.append({'entry': name, **inspect_elf(archive.read(name))})
    passed = all(item['static_16k_alignment'] for item in libraries)
    report = {'status': 'PASS' if passed else 'FAIL', 'apk_sha256': hashlib.sha256(apk.read_bytes()).hexdigest(),
              'scope': 'ELF LOAD alignment and offset/address congruence; not runtime 16KiB compatibility',
              'libraries': libraries, 'runtime_16k_device': 'NOT TESTED'}
    Path(args.output).write_text(json.dumps(report, indent=2) + '\n', encoding='utf-8')
    print(json.dumps({'status': report['status'], 'libraries': len(libraries)}))
    return 0 if passed else 1


if __name__ == '__main__':
    raise SystemExit(main())
