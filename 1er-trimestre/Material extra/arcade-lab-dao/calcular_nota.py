#!/usr/bin/env python3
"""Autocalificación ArcadeLab DAO: 9 puntos funcionales + 1 de JavaDoc condicionado (>85 %).

Una clase de test "Oculto<Nombre>" (solo con -Phidden-tests) cuenta en el bloque de <Nombre>.
Si VideojuegoDao filtra, ordena o recorta en Java (un for, un stream, un if dentro de
while (rs.next()), un subList...) sale un AVISO que NO cambia la nota: las consultas se
resuelven en SQL.
"""
from pathlib import Path
import re
import sys
import unicodedata
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent
REPORTS = ROOT / 'target' / 'surefire-reports'
PAQUETE = ROOT / 'src/main/java/es/ejemplo/arcade'
DOC_SOURCE = PAQUETE / 'dao/AbstractDao.java'
AVISO_SOURCE = PAQUETE / 'dao/VideojuegoDao.java'
PREFIJO_OCULTOS = 'Oculto'
TOTAL_FUNCIONAL = 9.0

# bloque: ([clases de test visibles], peso)
BLOQUES = {
    'abstractdao': (['AbstractDaoTest'], 2.0),
    'plataforma': (['PlataformaDaoTest'], 1.5),
    'videojuego': (['VideojuegoDaoTest'], 2.5),
    'consultas': (['ConsultasVideojuegoTest'], 1.5),
    'service': (['VideojuegoServiceTest'], 1.5),
}

SALIDA = []


def out(linea=''):
    SALIDA.append(linea)
    print(linea)


def coma(valor, decimales=2):
    return f'{valor:.{decimales}f}'.replace('.', ',')


def normalize(s):
    """Minúsculas y sin acentos: "Genérica" -> "generica"."""
    s = unicodedata.normalize('NFD', s.lower())
    return ''.join(c for c in s if unicodedata.category(c) != 'Mn')


def has_any(s, terms):
    return any(normalize(term) in s for term in terms)


def doc_before(text, signature):
    """JavaDoc /** ... */ inmediatamente anterior a la declaración (se admiten anotaciones)."""
    pattern = r'/\*\*((?:(?!\*/)[\s\S])*)\*/\s*(?:@\w+(?:\([^)]*\))?\s*)*' + signature
    matches = list(re.finditer(pattern, text))
    if not matches:
        return ''
    doc = matches[-1].group(1)
    doc = re.sub(r'^\s*\*\s?', '', doc, flags=re.M).strip()
    texto = normalize(doc)
    if re.search(r'\b(todo|pendiente|rellenar|documenta|documentar)\b', texto):
        return ''
    return texto if len(doc) >= 35 else ''


def javadoc_points():
    out('')
    out('DOCUMENTACIÓN JAVADOC — AbstractDao (máximo 1,00 punto)')
    if not DOC_SOURCE.is_file():
        out('  No se encuentra dao/AbstractDao.java: 0,00 puntos')
        return 0.0
    src = DOC_SOURCE.read_text(encoding='utf-8')
    clase = doc_before(src, r'public\s+abstract\s+class\s+AbstractDao\b')
    consultar = doc_before(src, r'public\s+List\s*<\s*T\s*>\s+consultar\s*\(')
    mapeo = doc_before(src, r'(?:protected\s+abstract|abstract\s+protected)\s+T\s+mapRsToClass\s*\(')

    clase_ok = bool(clase and has_any(clase, ('comun', 'generica', 'generico'))
                    and has_any(clase, ('dao',)))
    consultar_ok = bool(consultar and has_any(consultar, ('preparedstatement',))
                        and has_any(consultar, ('lista',))
                        and '@return' in consultar)
    mapeo_ok = bool(mapeo and has_any(mapeo, ('fila',))
                    and has_any(mapeo, ('objeto',))
                    and '@return' in mapeo)
    points = [(clase_ok, 0.2, 'Clase AbstractDao: común/genérica + dao'),
              (consultar_ok, 0.4, 'consultar: PreparedStatement + lista + @return'),
              (mapeo_ok, 0.4, 'mapRsToClass: fila + objeto + @return')]
    for ok, p, msg in points:
        out(f'  {"OK   " if ok else "FALTA"} {msg:<50} {coma(p if ok else 0)} / {coma(p)}')
    return round(sum(p for ok, p, _ in points if ok), 2)


def sin_comentarios_ni_textos(src):
    """Quita comentarios y deja los textos ("...", text blocks y 'c') vacíos."""
    patron = r'/\*[\s\S]*?\*/|//[^\n]*|"""[\s\S]*?"""|"(?:\\.|[^"\\\n])*"|\'(?:\\.|[^\'\\\n])+\''
    return re.sub(patron, lambda m: '' if m.group().startswith('/') else '""', src)


def cierre(src, i, abre, cierra):
    """Con src[i] == abre, devuelve la posición justo después de su cierre."""
    nivel = 0
    for j in range(i, len(src)):
        if src[j] == abre:
            nivel += 1
        elif src[j] == cierra:
            nivel -= 1
            if nivel == 0:
                return j + 1
    return len(src)


def cuerpos_rs_next(src):
    """Cuerpos de los while (rs.next()) (con llaves o de una sola sentencia)."""
    cuerpos = []
    for m in re.finditer(r'\bwhile\s*\(\s*\w+\.next\(\)\s*\)\s*', src):
        i = m.end()
        if src[i:i + 1] == '{':
            cuerpos.append(src[i + 1:cierre(src, i, '{', '}') - 1])
        else:
            fin = src.find(';', i)
            cuerpos.append(src[i:fin + 1 if fin >= 0 else len(src)])
    return cuerpos


def aviso_bucles():
    """WHERE, GROUP BY, HAVING, ORDER BY y LIMIT van en el SQL, nunca en Java. Solo avisa."""
    if not AVISO_SOURCE.is_file():
        return
    src = sin_comentarios_ni_textos(AVISO_SOURCE.read_text(encoding='utf-8'))
    encontrados = []
    for patron, nombre in ((r'\bfor\s*\(', 'for'), (r'\.stream\s*\(', '.stream()'),
                           (r'\.forEach\s*\(', '.forEach()'), (r'\.sort\s*\(', 'sort'),
                           (r'\.subList\s*\(', 'subList'), (r'\.removeIf\s*\(', 'removeIf'),
                           (r'\.remove\s*\(', 'remove'),
                           (r'\bCollections\s*\.\s*(?:max|min|reverse)\s*\(', 'Collections.max/min/reverse')):
        n = len(re.findall(patron, src))
        if n:
            encontrados.append(f'{nombre} x{n}' + (' (recortar es LIMIT ?)' if nombre == 'subList' else ''))
    otros_while = (len(re.findall(r'\bwhile\s*\(', src))
                   - len(re.findall(r'\bwhile\s*\(\s*\w+\.next\(\)\s*\)', src)))
    if otros_while > 0:
        encontrados.append(f'while x{otros_while} (aparte de while (rs.next()))')
    # Dentro de while (rs.next()) solo se lee la fila y se guarda. Un if que decide si se
    # guarda es un WHERE o un HAVING hecho en Java; break/continue, un LIMIT. Los if de
    # nulos (rs.wasNull(), != null) son lectura de la fila y no cuentan.
    filtros = cortes = 0
    for cuerpo in cuerpos_rs_next(src):
        for m in re.finditer(r'\bif\s*\(', cuerpo):
            condicion = cuerpo[m.end() - 1:cierre(cuerpo, m.end() - 1, '(', ')')]
            if 'null' not in condicion.lower():
                filtros += 1
        cortes += len(re.findall(r'\b(?:break|continue)\b', cuerpo))
    if filtros:
        encontrados.append(f'if dentro de while (rs.next()) x{filtros} (filtrar es WHERE o HAVING)')
    if cortes:
        encontrados.append(f'break/continue dentro de while (rs.next()) x{cortes}')
    if encontrados:
        out('')
        out('AVISO (no cambia la nota): VideojuegoDao usa Java para lo que debería hacer el SQL: '
            + ', '.join(encontrados) + '.')
        out('  Resuélvelo en la consulta (WHERE, GROUP BY, HAVING, ORDER BY, LIMIT ?). '
            'while (rs.next()) solo sirve para leer cada fila y guardarla tal cual: '
            'sin if que elija cuáles, sin break y sin subList.')


def leer_informes():
    """Devuelve {nombre_simple_de_clase: [(test, aprobado), ...]}."""
    informes = {}
    if not REPORTS.is_dir():
        return informes
    for report in sorted(REPORTS.glob('TEST-*.xml')):
        try:
            root = ET.parse(report).getroot()
        except ET.ParseError as exc:
            out(f'  {report.name}: XML de pruebas ilegible ({exc})')
            continue
        nombre = root.attrib.get('name', report.stem[5:]).split('.')[-1]
        casos = []
        for case in root.iter('testcase'):
            aprobado = not any(case.find(kind) is not None for kind in ('failure', 'error', 'skipped'))
            casos.append((case.attrib.get('name', '?'), aprobado))
        informes[nombre] = casos
    return informes


def calificar(informes, con_ocultos):
    points = 0.0
    total_pass = total_tests = 0
    all_reports = True
    suspensos = []
    out(f'  {"Bloque":<12} {"Pruebas":>9}   {"Puntos":>13}   Detalle')
    out('  ' + '-' * 72)
    for bloque, (clases, weight) in BLOQUES.items():
        n = passed = 0
        detalle = []
        for clase in clases:
            nombres = [clase] + ([PREFIJO_OCULTOS + clase] if con_ocultos else [])
            for nombre in nombres:
                casos = informes.get(nombre)
                if casos is None:
                    if nombre == clase:
                        all_reports = False
                        detalle.append(f'{nombre} SIN INFORME')
                    continue
                ok = sum(1 for _, a in casos if a)
                n += len(casos)
                passed += ok
                detalle.append(f'{nombre} {ok}/{len(casos)}')
                suspensos += [f'{nombre}.{t}' for t, a in casos if not a]
        if n == 0:
            all_reports = False
            out(f'  {bloque:<12} {"-":>9}   {coma(0)} / {coma(weight)}   [{", ".join(detalle)}]')
            continue
        score = weight * passed / n
        points += score
        total_pass += passed
        total_tests += n
        out(f'  {bloque:<12} {f"{passed}/{n}":>9}   {coma(score)} / {coma(weight)}   [{", ".join(detalle)}]')
    out('  ' + '-' * 72)
    percent = 100 * total_pass / total_tests if total_tests else 0.0
    return points, total_pass, total_tests, percent, all_reports, suspensos


def seccion(informes, con_ocultos, docs_previos=None):
    points, ok, n, percent, all_reports, suspensos = calificar(informes, con_ocultos)
    out(f'Funcionalidad: {coma(points)} / {coma(TOTAL_FUNCIONAL)}   '
        f'(pruebas superadas: {ok}/{n}, {coma(percent)} %)')
    eligible = all_reports and n > 0 and percent > 85
    if eligible:
        out('JavaDoc: ELEGIBLE (más del 85 % de pruebas superadas).')
        docs = docs_previos if docs_previos is not None else javadoc_points()
    else:
        out('JavaDoc: NO ELEGIBLE (hacen falta todos los informes y más del 85 % de pruebas superadas).')
        docs = 0.0
    out(f'JavaDoc: {coma(docs)} / 1,00')
    if suspensos:
        out('Pruebas no superadas:')
        for s in suspensos:
            out(f'  - {s}')
    if not all_reports:
        out('AVISO: faltan informes de pruebas. Revisa los errores de compilación o de ejecución.')
    return points + docs, (docs if eligible else None)


def main():
    informes = leer_informes()
    hay_ocultos = any(nombre.startswith(PREFIJO_OCULTOS) for nombre in informes)

    out('=' * 74)
    out('AUTOEVALUACIÓN ArcadeLab DAO — 9 puntos funcionales + 1 de JavaDoc')
    out('=' * 74)
    nota, docs = seccion(informes, con_ocultos=False)
    out('')
    out(f'NOTA FINAL: {coma(nota)} / 10')
    aviso_bucles()

    out('')
    if not hay_ocultos:
        out('TESTS OCULTOS: no incluidos (mvn -o clean verify -Pnota -Phidden-tests).')
    else:
        out('=' * 74)
        out('CON TESTS OCULTOS (cada Oculto<Clase> cuenta en el bloque de <Clase>)')
        out('=' * 74)
        nota_h, _ = seccion(informes, con_ocultos=True, docs_previos=docs)
        out('')
        out(f'NOTA FINAL (con ocultos): {coma(nota_h)} / 10')

    try:
        (ROOT / 'target').mkdir(parents=True, exist_ok=True)
        (ROOT / 'target' / 'nota.txt').write_text('\n'.join(SALIDA) + '\n', encoding='utf-8')
    except OSError:
        pass
    return 0


if __name__ == '__main__':
    sys.exit(main())
