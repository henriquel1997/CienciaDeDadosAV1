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