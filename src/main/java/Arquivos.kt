import java.io.File

fun readCSV(filepath: String, sep: String = ";"): MutableList<MutableList<String>> {
    val linhas = mutableListOf<MutableList<String>>()

    for(linhasTexto in File(filepath).readLines(Charsets.ISO_8859_1)){
        linhas.add(linhasTexto.split(sep, ",").toMutableList())
    }

    return linhas
}

fun writeCSV(filepath: String, csv: MutableList<MutableList<String>>, sep: String = ";"){
    File(filepath).printWriter(Charsets.ISO_8859_1).use { out ->
        csv.forEach{ linha ->
            var linhaTexto = ""

            linha.forEachIndexed { index, string ->
                if(index > 0){
                    linhaTexto += sep
                }
                linhaTexto += string
            }

            out.println(linhaTexto)
        }
    }
}

fun readTXTList(filepath: String): MutableList<String> {
    val lista = mutableListOf<String>()

    for(linhasTexto in File(filepath).readLines()){
        lista.add(linhasTexto)
    }
    return lista
}

fun getColumnIndex(csv: List<List<String>>, texto: String) = csv[0].indexOfFirst { it == texto }

fun addNewColumn(csv: MutableList<MutableList<String>>, nome: String, pos: Int = csv[0].size): Int {
    csv[0].add(pos, nome)
    for(i in 1 until csv.size){
        csv[i].add(pos, "")
    }
    return pos
}

fun tamanhoMaiorLinha(csv: List<List<*>>): Int {
    var maior = 0
    for(linha in csv){
        if(linha.size > maior){
            maior = linha.size
        }
    }
    return maior
}

fun MutableList<MutableList<String>>.igualarLinhas(){
    val maiorLinha = tamanhoMaiorLinha(this)
    for(linha in this){
        while(linha.size < maiorLinha){
            linha.add("")
        }
    }
}