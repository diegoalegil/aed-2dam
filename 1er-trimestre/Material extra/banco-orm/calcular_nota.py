#!/usr/bin/env python3
"""Evalúa los informes JUnit de Surefire y el JavaDoc de las interfaces, sin modificar el código del alumno."""
from pathlib import Path
import re
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent
REPORTS = ROOT / 'target' / 'surefire-reports'
SRC = ROOT / 'src' / 'main' / 'java' / 'es' / 'ejemplo' / 'banco' / 'orm' / 'repository'
INTERFACES = ('IRepository.java', 'IClienteRepository.java')
METODOS_DOC = 10

PESOS = {'montaje': 2.0, 'crud': 4.0, 'consultas': 3.0, 'documentacion': 1.0}
TITULOS = {'montaje': 'NIVEL 0: MONTAJE', 'crud': 'NIVEL 1: CLIENTE CRUD', 'consultas': 'NIVEL 1: CLIENTE CONSULTAS'}

# Bloque de cada clase de test según el prefijo de su nombre simple.
PREFIJOS_MONTAJE = ('OrmFactory', 'Nivel0')
PREFIJOS_CLIENTE = ('ClienteOrmRepository',)

# En las clases de cliente, estos métodos son consultas; el resto es CRUD.
CONSULTAS = {
    # visibles
    'buscarPorEmailCoincideTest', 'buscarPorEmailSinCoincidenciaTest',
    'buscarPorDniCoincideTest', 'buscarPorDniSinCoincidenciaTest',
    'listarPorNombreContieneOrdenadoTest', 'listarPorNombreSinCoincidenciaTest',
    'listarAltasDesdeCoincideTest', 'listarAltasDesdeSinCoincidenciaTest',
    'contarDevuelveNumeroDeClientesTest',
    # ocultos
    'buscarPorEmailNoUsaComodinesTest', 'buscarPorEmailNoAceptaCoincidenciaParcialTest',
    'listarPorNombreIgnoraMayusculasTest', 'listarPorNombreTextoEnBlancoDevuelveVacioTest',
    'listarPorNombreMayusculaConTildeTest',
    'listarAltasDesdeIncluyeLimiteYDesempataPorIdTest', 'listarAltasDesdeNulaDevuelveVacioTest',
    'contarTrasBorrarTest',
}

# Tests esperados por clase y bloque. Las clases *OcultoTest solo cuentan si existe su informe.
ESPERADOS = {
    'OrmFactoryTest': {'montaje': 7},
    'ClienteOrmRepositoryTest': {'crud': 16, 'consultas': 9},
    'OrmFactoryOcultoTest': {'montaje': 6},
    'ClienteOrmRepositoryOcultoTest': {'crud': 10, 'consultas': 8},
}


def normales(s):
    return s.translate(str.maketrans('áéíóúÁÉÍÓÚñÑ', 'aeiouAEIOUnN')).lower()


PALABRAS_CLAVE_API = {
    'findAll': [('listar', 'lista', 'listado')],
    'findById': [('id', 'identificador')],
    'create': [('crear', 'insertar', 'inserta', 'insercion', 'registrar')],
    'update': [('actualizar', 'actualiza', 'modificar', 'cambiar')],
    'delete': [('eliminar', 'elimina', 'borrar', 'suprimir')],
    'buscarPorEmail': [('email', 'correo')],
    'buscarPorDni': [('dni', 'documento')],
    'listarPorNombre': [('nombre',), ('contiene', 'contenga', 'fragmento', 'texto')],
    'listarAltasDesde': [('fecha',), ('alta', 'altas')],
    'contar': [('numero', 'total', 'cantidad', 'cuenta'), ('cliente', 'clientes')],
}


def palabras_clave_presentes(nombre_metodo, descripcion):
    texto = normales(descripcion)
    grupos = PALABRAS_CLAVE_API.get(nombre_metodo, [])
    return bool(grupos) and all(
        any(re.search(r'\b' + re.escape(palabra) + r'\b', texto) for palabra in grupo)
        for grupo in grupos
    )


def doc_metodos(archivo):
    if not archivo.exists():
        return []
    txt = archivo.read_text(encoding='utf8')
    patron = re.compile(r'(?P<javadoc>/\*\*[\s\S]*?\*/)?\s*(?:public\s+)?(?P<retorno>[\w<>\[\], ?]+)\s+'
                        r'(?P<metodo>\w+)\s*\((?P<params>[^)]*)\)\s*;', re.M)
    resultado = []
    for m in patron.finditer(txt):
        jd = m['javadoc'] or ''
        params = [p.strip().split()[-1] for p in m['params'].split(',') if p.strip()]
        contenido = normales(re.sub(r'[@*]', ' ', re.sub(r'^/\*\*|\*/$', '', jd.strip())))
        pts = 0
        descripcion = re.split(r'@(?:param|return|throws|see|since|deprecated)\b', jd, maxsplit=1)[0]
        descripcion = re.sub(r'^/\*\*|\*/$', '', descripcion.strip())
        if len(' '.join(contenido.split())) >= 25 and m['metodo'].lower() not in contenido.strip()[:10]:
            pts += .40
        if all(re.search(r'@param\s+' + re.escape(p) + r'\b', jd) for p in params):
            pts += .25
        if m['retorno'].strip() == 'void' or '@return' in jd:
            pts += .20
        if palabras_clave_presentes(m['metodo'], descripcion):
            pts += .15
        resultado.append((m['metodo'], pts))
    return resultado


def bloque_de(clase, metodo):
    if clase.startswith(PREFIJOS_MONTAJE):
        return 'montaje'
    if clase.startswith(PREFIJOS_CLIENTE):
        return 'consultas' if metodo in CONSULTAS else 'crud'
    return None


def lectura_tests():
    """Devuelve {clase: {metodo: superado}} con las clases de ESPERADOS que tienen informe."""
    resultados = {}
    if not REPORTS.exists():
        return resultados
    for file in REPORTS.glob('TEST-*.xml'):
        try:
            root = ET.parse(file).getroot()
        except ET.ParseError:
            continue
        clase = root.get('name', '').split('.')[-1]
        if clase not in ESPERADOS:
            continue
        for caso in root.iter('testcase'):
            metodo = caso.get('name', '').split('(')[0]
            correcto = not any(child.tag in ('failure', 'error', 'skipped') for child in caso)
            resultados.setdefault(clase, {})[metodo] = correcto
    return resultados


def nota():
    pruebas = lectura_tests()
    ocultas = [c for c in ESPERADOS if c.endswith('OcultoTest') and c in pruebas]
    clases = [c for c in ESPERADOS if not c.endswith('OcultoTest') or c in pruebas]

    lineas = ['=== NOTA POR BLOQUES ===', 'Comando: mvn clean verify -Pnota',
              'Tests ocultos: ' + (', '.join(ocultas) if ocultas else 'no presentes (solo tests visibles)'), '']
    superados = {b: 0 for b in TITULOS}
    esperados = {b: 0 for b in TITULOS}
    completos = {b: True for b in TITULOS}
    fallidos, avisos = [], []

    for clase in clases:
        resultados = pruebas.get(clase)
        if resultados is None:
            avisos.append(f'- {clase}: sin informe de Surefire (¿no compila o no se ha ejecutado?)')
            resultados = {}
        por_bloque = {}
        for metodo, ok in resultados.items():
            bloque = bloque_de(clase, metodo)
            if bloque:
                por_bloque.setdefault(bloque, {})[metodo] = ok
                if not ok:
                    fallidos.append(f'- {clase}.{metodo}')
        for bloque, n in ESPERADOS[clase].items():
            tests = por_bloque.get(bloque, {})
            ok = sum(tests.values())
            superados[bloque] += min(ok, n)
            esperados[bloque] += n
            if len(tests) != n or ok != n:
                completos[bloque] = False
            if tests and len(tests) != n:
                avisos.append(f'- {clase}: {len(tests)} tests de {TITULOS[bloque]} y se esperaban {n} '
                              '(no cambies los nombres de los tests)')

    partes = []
    for bloque, titulo in TITULOS.items():
        aporte = PESOS[bloque] * superados[bloque] / esperados[bloque] if esperados[bloque] else 0.0
        partes.append(aporte)
        lineas.append(f'=== {titulo} ===')
        lineas.append(f'Tests: {superados[bloque]}/{esperados[bloque]} superados; '
                      f'aportación {aporte:.2f}/{PESOS[bloque]:.2f}')
        if bloque == 'crud':
            lineas.append(f'CRUD completo: {"sí" if completos["crud"] else "no"}')
        lineas.append('')

    docs = []
    for nombre in INTERFACES:
        archivo = SRC / nombre
        docs.extend((archivo.name, metodo, pt) for metodo, pt in doc_metodos(archivo))
    doc_media = sum(x[2] for x in docs) / METODOS_DOC if len(docs) == METODOS_DOC else 0.0
    n_doc = PESOS['documentacion'] * doc_media if completos['crud'] else 0.0
    partes.append(n_doc)
    lineas.extend(['=== DOCUMENTACIÓN ===',
                   f'JavaDoc: {len(docs)}/{METODOS_DOC} métodos detectados en IRepository e IClienteRepository; '
                   f'calidad {doc_media * 10:.2f}/10',
                   f'CRUD de clientes completo: {"sí" if completos["crud"] else "no"}; '
                   f'aportación {n_doc:.2f}/{PESOS["documentacion"]:.2f}'])
    for archivo, metodo, pt in docs:
        if pt < .999:
            lineas.append(f'- {archivo}.{metodo}: {pt:.2f}/1.00')

    if fallidos:
        lineas.extend(['', '=== TESTS NO SUPERADOS ==='] + sorted(fallidos))
    if avisos:
        lineas.extend(['', '=== AVISOS ==='] + avisos)

    total = round(sum(partes), 2)
    lineas.extend(['', f'NOTA FINAL: {total:.2f}/10.00', ''])
    informe = '\n'.join(lineas)
    target = ROOT / 'target'
    target.mkdir(exist_ok=True)
    (target / 'notas_por_bloques.txt').write_text(informe, encoding='utf8')
    print(informe)
    return 0


if __name__ == '__main__':
    raise SystemExit(nota())
