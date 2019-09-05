import java.io.File
import java.util.*
import java.util.Calendar.*
import kotlin.math.max
import kotlin.text.Charsets.ISO_8859_1

const val NULL = "NULL"

fun main(){
    val path = "C:\\Users\\Henrique\\Documents\\Unifor\\Ciência de Dados\\"
    //baseAlunos()
    //baseDengue()
    //baseOnibus()
    //contarIdades("${path}Base de Alunos3 Editado.csv", "${path}Histograma Idades Aluno.csv")
    //contarIdades("${path}Base de Onibus3 Editado.csv", "${path}Histograma Idades Onibus.csv")
    //contarIdades("${path}Base de Dengue3 Editado.csv", "${path}Histograma Idades Dengue.csv")
    parearBairros("${path}bairros.txt","${path}Base de Alunos3 Editado.csv", "${path}Base de Alunos3 Pareamento Bairros.csv")
    parearBairros("${path}bairros.txt","${path}Base de Onibus3 Editado.csv", "${path}Base de Onibus3 Pareamento Bairros.csv")
    parearBairros("${path}bairros.txt","${path}Base de Dengue3 Editado.csv", "${path}Base de Dengue3 Pareamento Bairros.csv")
}

fun parearBairros(bairrosListPath: String, sourcePath: String, endPath: String){
    val csv = readCSV(sourcePath)
    val colunaBairro = getColumnIndex(csv, "Bairro")
    if(colunaBairro > -1){
        val bairros = readTXTList(bairrosListPath)
        val colunaPorcSimilar = addNewColumn(csv, "porcSimilar")
        val colunaBairroPareado = addNewColumn(csv, "bairroPareado")

        for(i in 1 until csv.size){
            val bairroAtual = csv[i][colunaBairro]

            var maiorPorc = 0.0
            var bairroMaisParecido = NULL
            for(bairro in bairros){
                val porc = levenshteinPercentage(bairro, bairroAtual)
                if(porc > maiorPorc){
                    maiorPorc = porc
                    bairroMaisParecido = bairro
                }
            }

            csv[i][colunaPorcSimilar] = maiorPorc.toString()
            csv[i][colunaBairroPareado] = bairroMaisParecido
        }

        writeCSV(endPath, csv)
    }
}

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
            matriz[i][j] = if(string1[i - 1] == string2[j - 1]){
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

fun contarIdades(sourcePath: String, endPath: String){
    val csv = readCSV(sourcePath)
    val colunaIdade = getColumnIndex(csv, "idade")

    val mapa = hashMapOf<Int, Int>()
    for(i in 1 until csv.size){
        csv[i][colunaIdade].toIntOrNull()?.let { idade ->
            if(!mapa.containsKey(idade)){
                mapa[idade] = 1
            }else{
                mapa[idade]?.let { mapa[idade] = it + 1 }
            }
        }
    }

    val novoCSV = mutableListOf<MutableList<String>>()
    novoCSV.add(mutableListOf("idade", "quantidade"))

    val mapaOrdenado = mapa.toSortedMap()
    for(key in mapaOrdenado.keys){
        novoCSV.add(mutableListOf(key.toString(), mapaOrdenado[key].toString()))
    }

    writeCSV(endPath, novoCSV)
}

fun baseAlunos(){
    val csv = readCSV("C:\\Users\\Henrique\\Documents\\Unifor\\Ciência de Dados\\Base de Alunos3.csv")
    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaDataEditada = addNewColumn(csv, "dataEditada")
    val colunaIdade = addNewColumn(csv, "idade")

    val calendar = Calendar.getInstance()

    var linhasSemData = 0

    for(i in 1 until csv.size){
        val (data, idade) = processarData(csv[i][colunaData], calendar, i, sep = "-")
        csv[i][colunaDataEditada] = data
        csv[i][colunaIdade] = idade.toString()

        if(data == NULL){
            linhasSemData++
        }
    }

    println("Linhas sem data: $linhasSemData")

    writeCSV("C:\\Users\\Henrique\\Documents\\Unifor\\Ciência de Dados\\Base de Alunos3 Editado.csv", csv)
}

fun baseDengue(){
    val csv = readCSV("C:\\Users\\Henrique\\Documents\\Unifor\\Ciência de Dados\\Base de Dengue3.csv")
    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaDataEditada = addNewColumn(csv, "dataEditada")
    val colunaIdade = addNewColumn(csv, "idade")
    val colunaDataDengue = getColumnIndex(csv, "Data da Dengue")
    val colunaDataDengueEditada = addNewColumn(csv, "dataDengueEditada")

    val calendar = Calendar.getInstance()

    var linhasSemData = 0
    var linhasDengueSemData = 0

    for(i in 1 until csv.size){
        val (data, idade) = processarData(csv[i][colunaData], calendar, i, sep = "/")
        csv[i][colunaDataEditada] = data
        csv[i][colunaIdade] = idade.toString()

        val (dataDengue, _) = processarData(csv[i][colunaDataDengue], calendar, i, sep = "/")
        csv[i][colunaDataDengueEditada] = dataDengue

        if(data == NULL){
            linhasSemData++
        }
        if(dataDengue == NULL){
            linhasDengueSemData++
        }
    }

    println("Linhas sem data de nascimento: $linhasSemData")
    println("Linhas sem data da dengue: $linhasDengueSemData")

    writeCSV("C:\\Users\\Henrique\\Documents\\Unifor\\Ciência de Dados\\Base de Dengue3 Editado.csv", csv)
}

fun baseOnibus(){
    val csv = readCSV("C:\\Users\\Henrique\\Documents\\Unifor\\Ciência de Dados\\Base de Onibus3.csv")
    csv.igualarLinhas()
    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaDataEditada = addNewColumn(csv, "dataEditada")
    val colunaIdade = addNewColumn(csv, "idade")

    val calendar = Calendar.getInstance()

    var linhasSemData = 0

    for(i in 1 until csv.size){
        val (data, idade) = processarDataSemBarra(csv[i][colunaData], calendar, i)
        csv[i][colunaDataEditada] = data
        csv[i][colunaIdade] = idade.toString()

        if(data == NULL){
            linhasSemData++
        }
    }

    println("Linhas sem data: $linhasSemData")

    writeCSV("C:\\Users\\Henrique\\Documents\\Unifor\\Ciência de Dados\\Base de Onibus3 Editado.csv", csv)
}

fun readCSV(filepath: String, sep: String = ";"): MutableList<MutableList<String>> {
    val linhas = mutableListOf<MutableList<String>>()

    for(linhasTexto in File(filepath).readLines(ISO_8859_1)){
        linhas.add(linhasTexto.split(sep, ",").toMutableList())
    }

    return linhas
}

fun writeCSV(filepath: String, csv: MutableList<MutableList<String>>, sep: String = ";"){
    File(filepath).printWriter(ISO_8859_1).use { out ->
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

fun setupDateString(string: String): String {
    if(string.length < 2){
        return "0$string"
    }
    return string
}

fun getYearDiff(a: Calendar, b: Calendar): Int {
    var diff = b.get(YEAR) - a.get(YEAR)

    if(a.get(MONTH) > b.get(MONTH) || (a.get(MONTH) == b.get(MONTH)) && (a.get(DATE) > b.get(DATE))){
        diff--
    }

    return diff
}

fun processarData(data: String, calendar: Calendar, index: Int, anoMin: Int = 0, sep: String = "/", limitarPeloDiaAtual: Boolean = false): Pair<String, Int> {
    val valores = data.split(sep, ":", " ")

    if(valores.size >= 3){
        valores[0].toIntOrNull()?.let { dia ->
            valores[1].toIntOrNull()?.let { mes ->
                valores[2].toIntOrNull()?.let { ano ->
                    if(ano > anoMin && mes > 0 && mes <= 12){
                        val calData = GregorianCalendar(ano, mes - 1, 1)
                        val diaMax = calData.getActualMaximum(Calendar.DAY_OF_MONTH)
                        if(dia in 1..diaMax){
                            val dataAtual = calendar.timeInMillis
                            val dataValor = calData.timeInMillis
                            if(!limitarPeloDiaAtual || dataValor < dataAtual){
                                val dataPadronizada = setupDateString(valores[2])+setupDateString(valores[1])+setupDateString(valores[0])
                                val idade = getYearDiff(calData, calendar)
                                return Pair(dataPadronizada, idade)
                            }
                        }
                    }
                }
            }
        }
    }

    println("$index - Data errada: $data")

    return Pair(NULL, Integer.MIN_VALUE)
}

fun processarDataSemBarra(data: String, calendar: Calendar, index: Int, anoMin: Int = 0): Pair<String, Int> {

    data.toIntOrNull()?.let { numero ->
        val dataSemZero = numero.toString()
        when(dataSemZero.length){
            6 -> {
                val dataComBarra = dataSemZero.substring(0, 1) + "/" + dataSemZero.substring(1, 2) + "/" + dataSemZero.substring(2, dataSemZero.length)
                return processarData(dataComBarra, calendar, index, anoMin)
            }
            7 -> {
                val dataComUltimaBarra = dataSemZero.substring(0, 3) + "/" + dataSemZero.substring(3, dataSemZero.length)
                val dataComDoisDigDia = dataComUltimaBarra.substring(0, 2) + "/" + dataComUltimaBarra.substring(2, dataComUltimaBarra.length)
                val parDoisDig = processarData(dataComDoisDigDia, calendar, index, anoMin)
                if(parDoisDig.first != NULL){
                    return parDoisDig
                }

                val dataComUmDigDia = dataComUltimaBarra.substring(0, 1) + "/" + dataComUltimaBarra.substring(1, dataComUltimaBarra.length)
                return processarData(dataComUmDigDia, calendar, index, anoMin)

            }
            8 -> {
                val dataComBarra = dataSemZero.substring(0, 2) + "/" + dataSemZero.substring(2, 4) + "/" + dataSemZero.substring(4, dataSemZero.length)
                return processarData(dataComBarra, calendar, index, anoMin)
            }

            else -> {}
        }
    }

    println("$index - Data errada: $data")

    return Pair(NULL, Integer.MIN_VALUE)
}