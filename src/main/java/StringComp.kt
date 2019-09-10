import java.util.*
import kotlin.math.max

fun levenshteinPercentage(string1: String, string2: String) = 1.0 - (levenshteinDistance(string1, string2).toDouble() / max(string1.length.toDouble(), string2.length.toDouble()))

fun levenshteinDistance(string1: String, string2: String): Int {

    val numLinhas = string1.length + 1
    val numColunas = string2.length + 1

    val matriz = Array(numLinhas){ IntArray(numColunas) }

    //Inicializar a linha e a coluna dos vazios
    for(i in 0 until numLinhas){
        matriz[i][0] = i
    }

    for(i in 0 until numColunas){
        matriz[0][i] = i
    }

    for(i in 1 until numLinhas){
        for(j in 1 until numColunas){
            matriz[i][j] = if(compararChar(string1[i - 1], string2[j - 1])){
                matriz[i - 1][j - 1]
            }else{
                val replace = matriz[i - 1][j - 1]
                val insert = matriz[i][j - 1]
                val delete = matriz[i - 1][j]

                minOf(replace, insert, delete) + 1
            }
        }
    }

    return matriz[numLinhas - 1][numColunas - 1]
}

fun jaroWinklerSimilarity(string1: String, string2: String): Double {
    val defaultScalingFactor = 0.1
    val percentageRoundValue = 100.0

    val mtp = matches(string1, string2)
    val matches = mtp[0].toDouble()
    val transpositions = mtp[1]
    val prefix = mtp[2]
    val maxLength = mtp[3]

    if (matches == 0.0) {
        return 0.0
    }
    val jaro = (matches / string1.length + matches / string2.length + (matches - transpositions) / matches) / 3
    val jaroWinkler = if (jaro < 0.7) jaro else jaro + Math.min(defaultScalingFactor, 1.0 / maxLength) * prefix.toDouble() * (1.0 - jaro)
    return Math.round(jaroWinkler * percentageRoundValue) / percentageRoundValue
}


fun matches(string1: String, string2: String): IntArray {
    val max: CharSequence
    val min: CharSequence
    if (string1.length > string2.length) {
        max = string1
        min = string2
    } else {
        max = string2
        min = string1
    }
    val range = Math.max(max.length / 2 - 1, 0)
    val matchIndexes = IntArray(min.length)
    Arrays.fill(matchIndexes, -1)
    val matchFlags = BooleanArray(max.length)
    var matches = 0
    for (mi in 0 until min.length) {
        val c1 = min[mi]
        var xi = Math.max(mi - range, 0)
        val xn = Math.min(mi + range + 1, max.length)
        while (xi < xn) {
            if (!matchFlags[xi] && c1 == max[xi]) {
                matchIndexes[mi] = xi
                matchFlags[xi] = true
                matches++
                break
            }
            xi++
        }
    }
    val ms1 = CharArray(matches)
    val ms2 = CharArray(matches)
    run {
        var i = 0
        var si = 0
        while (i < min.length) {
            if (matchIndexes[i] != -1) {
                ms1[si] = min[i]
                si++
            }
            i++
        }
    }
    var i = 0
    var si = 0
    while (i < max.length) {
        if (matchFlags[i]) {
            ms2[si] = max[i]
            si++
        }
        i++
    }
    var transpositions = 0
    for (mi in ms1.indices) {
        if (ms1[mi] != ms2[mi]) {
            transpositions++
        }
    }
    var prefix = 0
    for (mi in 0 until min.length) {
        if (string1[mi] == string2[mi]) {
            prefix++
        } else {
            break
        }
    }
    return intArrayOf(matches, transpositions / 2, prefix, max.length)
}

fun compararChar(c1: Char, c2: Char): Boolean {
    return converterParaLetraComum(c1) == converterParaLetraComum(c2)
}

fun converterParaLetraComum(c: Char) = when(c.toLowerCase()){
    'á', 'à', 'â', 'ã' -> 'a'
    'é', 'è', 'ê' -> 'e'
    'í', 'ì', 'î' -> 'i'
    'ó', 'ò', 'ô', 'õ' -> 'o'
    'ú', 'ù', 'û' -> 'u'
    'ç' -> 'c'
    else -> c
}

fun String.removerAcentosELowerCase(): String {
    var semAcento = ""
    for(letra in this){
        semAcento += converterParaLetraComum(letra)
    }
    return semAcento
}

fun String.padronizarNome(): String {
    var nomePadronizado = ""
    this.split("\\s".toRegex()).forEachIndexed { index, nome ->
        if(index > 0){
            nomePadronizado += " "
        }
        nomePadronizado += nome.removerAcentosELowerCase().replace("[^a-zA-Z]".toRegex(), "")
    }
    return nomePadronizado
}