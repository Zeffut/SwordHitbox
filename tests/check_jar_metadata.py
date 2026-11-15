#!/usr/bin/env python3
"""Validate built binary JAR metadata for every Stonecutter target (not loader integration)."""
import json
import pathlib
import tomllib
import zipfile

root = pathlib.Path(__file__).resolve().parents[1]
nodes = sorted((root / 'versions').glob('*/gradle.properties'))
assert len(nodes) == 14, f'expected 14 targets, got {len(nodes)}'
for properties in nodes:
    node = properties.parent
    values = dict(line.split('=', 1) for line in properties.read_text().splitlines()
                  if line and not line.startswith('#') and '=' in line)
    jars = [p for p in (node / 'build/libs').glob('*.jar')
            if 'sources' not in p.name and 'dev' not in p.name]
    assert len(jars) == 1, (node.name, jars)
    with zipfile.ZipFile(jars[0]) as jar:
        assert not any('smoke' in name.lower() for name in jar.namelist()), node.name
        if node.name.endswith('-fabric'):
            data = json.loads(jar.read('fabric.mod.json'))
            assert data['depends']['minecraft'] == values['deps.minecraft'], node.name
            assert data['depends']['cloth-config'] == '>=' + values['deps.cloth_config'], node.name
            assert 'smoke' not in json.dumps(data).lower(), node.name
        else:
            data = tomllib.loads(jar.read('META-INF/neoforge.mods.toml').decode())
            deps = {d['modId']: d for d in data['dependencies']['swordhitbox']}
            assert deps['minecraft']['type'] == 'required', node.name
            assert deps['minecraft']['versionRange'] == '[' + values['deps.minecraft'] + ']', node.name
            assert deps['cloth_config']['type'] == 'required', node.name
            assert deps['cloth_config']['versionRange'] == '[' + values['deps.cloth_config'] + ',)', node.name
            assert 'smoke' not in str(data).lower(), node.name
    print('PASS', node.name, jars[0].name)
print('PASS: 14 binary JAR metadata checks; loader refusal not tested')
