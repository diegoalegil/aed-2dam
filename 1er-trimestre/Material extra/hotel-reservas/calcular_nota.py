#!/usr/bin/env python3
"""Nota del simulacro del hotel: 9 puntos de tests + 1 punto de JavaDoc.

Uso, desde la carpeta del proyecto (la que tiene el pom.xml):
    python3 -I calcular_nota.py               ejecuta "mvn -o clean test" y calcula la nota
    python3 -I calcular_nota.py --sin-maven   solo lee target/surefire-reports (sin volver a ejecutar)

Reglas:
  - Si el proyecto NO COMPILA (src/main o src/test), la nota es 0.
  - Tests: 9 * (tests superados / tests totales). Cuentan todos los tests que haya (visibles y,
    si se han copiado, los ocultos: sus clases empiezan por "Oculto").
  - JavaDoc (1 punto): solo si pasan MAS del 85 % de los tests. Se comprueba que cada metodo y
    cada constructor "public" de src/main/java tenga un comentario /** ... */ justo encima (se
    pueden poner anotaciones como @Override entre el comentario y el metodo). Puntua la parte
    proporcional de los que lo tienen, pero si CUALQUIER JavaDoc de src/main contiene la palabra
    "todo" o "pendiente" (en mayusculas o minusculas), el JavaDoc vale 0. Igual que el corrector
    del profe, tambien anulan "rellenar" y "documentar".
  - Los AVISOS del final no cambian la nota.
"""
from pathlib import Path
import re
import shutil
import subprocess
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parent
REPORTS = ROOT / 'target' / 'surefire-reports'
MAIN = ROOT / 'src' / 'main' / 'java'
PREFIJO_OCULTOS = 'Oculto'
PUNTOS_TESTS = 9.0
PUNTOS_JAVADOC = 1.0
UMBRAL = 85.0

SALIDA = []


def out(linea=''):
    SALIDA.append(linea)
    print(linea)


def coma(valor):
    return f'{valor:.2f}'.replace('.', ',')


def guardar():
    try:
        (ROOT / 'target').mkdir(parents=True, exist_ok=True)
        (ROOT / 'target' / 'nota.txt').write_text('\n'.join(SALIDA) + '\n', encoding='utf-8')
    except OSError:
        pass


# ----------------------------------------------------------------------------- Maven

def ejecutar_maven():
    """Lanza mvn -o clean test sin parar por tests rojos. Devuelve (codigo, salida)."""
    mvn = shutil.which('mvn') or shutil.which('mvn.cmd')
    if mvn is None:
        out('No se encuentra "mvn" en el PATH. Instala Maven o ejecuta los tests desde IntelliJ')
        out('y despues: python3 -I calcular_nota.py --sin-maven')
        return None, ''
    out('Ejecutando: mvn -o clean test  (puede tardar un poco)...')
    proc = subprocess.run([mvn, '-o', 'clean', 'test', '-Dmaven.test.failure.ignore=true'],
                          cwd=ROOT, capture_output=True, text=True)
    return proc.returncode, proc.stdout + proc.stderr


def errores_de_compilacion(salida):
    lineas = []
    for linea in salida.splitlines():
        if linea.startswith('[ERROR]') and ('.java' in linea or 'COMPILATION' in linea or 'symbol' in linea):
            lineas.append(linea.replace(str(ROOT) + '/', ''))
    return lineas[:25]


# ----------------------------------------------------------------------------- informes

def leer_informes():
    """{clase: [(test, aprobado, motivo)]} a partir de target/surefire-reports/TEST-*.xml."""
    informes = {}
    if not REPORTS.is_dir():
        return informes
    for informe in sorted(REPORTS.glob('TEST-*.xml')):
        try:
            raiz = ET.parse(informe).getroot()
        except ET.ParseError:
            continue
        clase = raiz.attrib.get('name', informe.stem[5:]).split('.')[-1]
        casos = []
        for caso in raiz.iter('testcase'):
            motivo = ''
            for tipo in ('failure', 'error', 'skipped'):
                nodo = caso.find(tipo)
                if nodo is not None:
                    mensaje = (nodo.attrib.get('message') or '').strip()
                    primera = mensaje.splitlines()[0] if mensaje else ''
                    motivo = nodo.attrib.get('type', tipo).split('.')[-1] + (': ' + primera if primera else '')
                    break
            casos.append((caso.attrib.get('name', '?'), motivo == '', motivo[:150]))
        informes[clase] = casos
    return informes


# ----------------------------------------------------------------------------- JavaDoc

DECLARACION = re.compile(r'^\s*public\s+(?!class\b|interface\b|enum\b|record\b|abstract\s+class\b|final\s+class\b)'
                         r'[^=;]*?\b(\w+)\s*\(')
JAVADOC = re.compile(r'/\*\*[\s\S]*?\*/')
PALABRA_TODO = re.compile(r'\b(todo|pendiente|rellenar|documentar)\b', re.IGNORECASE)


def javadoc_encima(lineas, i):
    """Texto del JavaDoc que esta justo encima de la linea i (saltando anotaciones), o None."""
    j = i - 1
    while j >= 0 and (not lineas[j].strip() or lineas[j].strip().startswith('@')):
        j -= 1
    if j < 0 or not lineas[j].rstrip().endswith('*/'):
        return None
    fin = j
    while j >= 0 and '/*' not in lineas[j]:
        j -= 1
    if j < 0 or '/**' not in lineas[j]:
        return None
    return '\n'.join(lineas[j:fin + 1])


def revisar_javadoc():
    """Devuelve (con_doc, total, sin_doc[], con_todo[])."""
    con_doc = total = 0
    sin_doc, con_todo = [], []
    for fichero in sorted(MAIN.rglob('*.java')):
        texto = fichero.read_text(encoding='utf-8')
        relativo = fichero.relative_to(ROOT)
        for m in JAVADOC.finditer(texto):
            prohibida = PALABRA_TODO.search(m.group())
            if prohibida:
                con_todo.append(f'{relativo}:{texto.count(chr(10), 0, m.start()) + 1}  ("{prohibida.group()}")')
        lineas = texto.splitlines()
        en_comentario = False
        for i, linea in enumerate(lineas):
            limpia = linea.strip()
            if en_comentario:
                if '*/' in limpia:
                    en_comentario = False
                continue
            if limpia.startswith('/*') and '*/' not in limpia:
                en_comentario = True
                continue
            m = DECLARACION.match(linea)
            if not m:
                continue
            total += 1
            if javadoc_encima(lineas, i) is not None:
                con_doc += 1
            else:
                sin_doc.append(f'{relativo}:{i + 1}  {limpia[:70]}')
    return con_doc, total, sin_doc, con_todo


def puntos_javadoc():
    con_doc, total, sin_doc, con_todo = revisar_javadoc()
    out(f'  Metodos y constructores public con JavaDoc: {con_doc}/{total}')
    if sin_doc:
        out('  Sin JavaDoc justo encima:')
        for s in sin_doc[:30]:
            out(f'    - {s}')
        if len(sin_doc) > 30:
            out(f'    ... y {len(sin_doc) - 30} mas')
    if con_todo:
        out('  JavaDoc con una palabra prohibida: todo, pendiente, rellenar o documentar (anula el punto entero):')
        for s in con_todo:
            out(f'    - {s}')
        return 0.0
    if total == 0:
        return 0.0
    return round(PUNTOS_JAVADOC * con_doc / total, 2)


# ----------------------------------------------------------------------------- avisos

PATRON_COMENTARIOS_Y_TEXTOS = r'/\*[\s\S]*?\*/|//[^\n]*|"""[\s\S]*?"""|"(?:\\.|[^"\\\n])*"|\'(?:\\.|[^\'\\\n])+\''
# un texto de SQL que acaba en operador (o en comilla) y al que se le pega una variable: "... = " + id
TEXTO_MAS_VARIABLE = re.compile(r'"((?:\\.|[^"\\\n])*)"\s*\+\s*[A-Za-z_]')
PALABRA_SQL = re.compile(r'\b(SELECT|INSERT|UPDATE|DELETE|WHERE|HAVING|VALUES|SET|AND|OR|BETWEEN|LIKE|LIMIT)\b')
FINAL_SQL = re.compile(r'(?:[=<>\']|\b(?:AND|OR|BETWEEN|LIKE|LIMIT|OFFSET))\s*$')


def concatena_sql(src):
    for m in TEXTO_MAS_VARIABLE.finditer(src):
        if PALABRA_SQL.search(m.group(1)) and FINAL_SQL.search(m.group(1)):
            return True
    return False


def sin_comentarios_ni_textos(src):
    return re.sub(PATRON_COMENTARIOS_Y_TEXTOS, lambda m: '' if m.group().startswith('/') else '""', src)


def sin_comentarios(src):
    return re.sub(PATRON_COMENTARIOS_Y_TEXTOS, lambda m: '' if m.group().startswith('/') else m.group(), src)


def cuerpo_desde(src, inicio):
    nivel = 0
    for k in range(inicio, len(src)):
        if src[k] == '{':
            nivel += 1
        elif src[k] == '}':
            nivel -= 1
            if nivel == 0:
                return src[inicio:k + 1]
    return src[inicio:]


def avisos():
    lista = []
    for fichero in sorted(MAIN.rglob('*.java')):
        original = fichero.read_text(encoding='utf-8')
        src = sin_comentarios_ni_textos(original)
        nombre = fichero.name
        if nombre.endswith('Dao.java') and concatena_sql(sin_comentarios(original)):
            lista.append(f'{nombre}: concatena un valor en el SQL ("... = " + variable). Siempre ? y setObject.')
        if re.search(r'\)\s*throws\s+\w', src):
            lista.append(f'{nombre}: hay "throws" en alguna cabecera. El profe no quiere subir excepciones: '
                         'try/catch dentro y throw new DaoException(mensaje, e).')
        for m in re.finditer(r'\bmap\s*\(\s*ResultSet\s+\w+\s*\)\s*\{', src):
            cuerpo = cuerpo_desde(src, m.end() - 1)
            if re.search(r'\.get(?:Int|String|BigDecimal|Double|Long|Object|Boolean)\s*\(\s*\d+\s*\)', cuerpo):
                lista.append(f'{nombre}: map lee columnas por POSICION (getXxx(1)...). Hay que leerlas por NOMBRE.')
        if (nombre.endswith('Dao.java') and not nombre.startswith('I') and nombre != 'AbstractDao.java'
                and not re.search(r'\bextends\s+AbstractDao\s*<', src)):
            lista.append(f'{nombre}: no hereda de AbstractDao<...>. El JDBC tiene que estar en el padre.')
        if nombre == 'AbstractDao.java':
            if not re.search(r'\babstract\s+class\s+AbstractDao\s*<\s*T\s*>', src):
                lista.append('AbstractDao.java: no es "abstract class AbstractDao<T>".')
            if not re.search(r'abstract\s+List\s*<\s*T\s*>\s+map\s*\(\s*ResultSet', src):
                lista.append('AbstractDao.java: falta "protected abstract List<T> map(ResultSet rs)".')
    if lista:
        out('')
        out('AVISOS (no cambian la nota, pero el profe los mira):')
        for a in lista:
            out(f'  - {a}')


# ----------------------------------------------------------------------------- nota

def main():
    sin_maven = '--sin-maven' in sys.argv[1:]
    out('=' * 74)
    out('SIMULACRO FINAL HOTEL: 9 puntos de tests + 1 de JavaDoc (si pasan mas del 85 %)')
    out('=' * 74)
    salida_mvn = ''
    if not sin_maven:
        codigo, salida_mvn = ejecutar_maven()
        if codigo is None:
            guardar()
            return 0
    informes = leer_informes()
    total = sum(len(c) for c in informes.values())
    if total == 0:
        out('')
        out('*** EL PROYECTO NO COMPILA (o no hay informes de tests): NOTA 0,00 / 10 ***')
        errores = errores_de_compilacion(salida_mvn)
        if errores:
            out('Errores de compilacion (los primeros):')
            for e in errores:
                out(f'  {e}')
        elif 'offline' in salida_mvn.lower():
            out('Maven no encuentra alguna dependencia en modo offline: ejecuta una vez "mvn test" con internet.')
        out('')
        out('NOTA FINAL: 0,00 / 10')
        guardar()
        return 0

    aprobados = 0
    suspensos = []
    vis_ok = vis_n = ocu_ok = ocu_n = 0
    out('')
    out(f'  {"Clase de test":<28} {"Superados":>10}')
    out('  ' + '-' * 40)
    for clase in sorted(informes, key=lambda c: (c.startswith(PREFIJO_OCULTOS), c)):
        casos = informes[clase]
        ok = sum(1 for _, a, _ in casos if a)
        aprobados += ok
        if clase.startswith(PREFIJO_OCULTOS):
            ocu_ok, ocu_n = ocu_ok + ok, ocu_n + len(casos)
        else:
            vis_ok, vis_n = vis_ok + ok, vis_n + len(casos)
        out(f'  {clase:<28} {f"{ok}/{len(casos)}":>10}')
        suspensos += [(f'{clase}.{t}', motivo) for t, a, motivo in casos if not a]
    out('  ' + '-' * 40)
    porcentaje = 100.0 * aprobados / total
    out(f'  Visibles: {vis_ok}/{vis_n}' + (f'   Ocultos: {ocu_ok}/{ocu_n}' if ocu_n else '   (sin tests ocultos)'))
    out(f'  TOTAL: {aprobados}/{total} ({coma(porcentaje)} %)')
    nota_tests = round(PUNTOS_TESTS * aprobados / total, 2)
    out('')
    out(f'Tests: {coma(nota_tests)} / {coma(PUNTOS_TESTS)}')

    out('')
    if porcentaje > UMBRAL:
        out(f'JavaDoc: ELEGIBLE (pasan mas del {UMBRAL:.0f} % de los tests).')
        nota_doc = puntos_javadoc()
    else:
        out(f'JavaDoc: NO ELEGIBLE (hace falta pasar mas del {UMBRAL:.0f} % de los tests).')
        nota_doc = 0.0
    out(f'JavaDoc: {coma(nota_doc)} / {coma(PUNTOS_JAVADOC)}')

    if suspensos:
        out('')
        out('Tests no superados:')
        for nombre, motivo in suspensos:
            out(f'  - {nombre}' + (f'   [{motivo}]' if motivo else ''))
    avisos()
    out('')
    out(f'NOTA FINAL: {coma(nota_tests + nota_doc)} / 10')
    guardar()
    return 0


if __name__ == '__main__':
    sys.exit(main())
