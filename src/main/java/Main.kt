import java.io.File
import java.util.*
import kotlin.text.Charsets.ISO_8859_1

const val NULL = "NULL"

fun main(){
    //baseAlunos()
    //baseDengue()
    baseOnibus()
}

fun baseAlunos(){
    val csv = readCSV("C:\\Users\\Henrique\\Documents\\Unifor\\Ciência de Dados\\Base de Alunos3.csv")
    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaDataEditada = addNewColumn(csv, "dataEditada")

    val calendar = Calendar.getInstance()

    var linhasSemData = 0

    for(i in 1 until csv.size){
        val data = processarData(csv[i][colunaData], calendar, i, anoMin = 1940, sep = "-")
        csv[i][colunaDataEditada] = data

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
    val colunaDataDengue = getColumnIndex(csv, "Data da Dengue")
    val colunaDataDengueEditada = addNewColumn(csv, "dataDengueEditada")

    val calendar = Calendar.getInstance()

    var linhasSemData = 0
    var linhasDengueSemData = 0

    for(i in 1 until csv.size){
        val data = processarData(csv[i][colunaData], calendar, i, anoMin = 1900, sep = "/")
        csv[i][colunaDataEditada] = data

        val dataDengue = processarData(csv[i][colunaDataDengue], calendar, i, anoMin = 1900, sep = "/")
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

    val calendar = Calendar.getInstance()

    var linhasSemData = 0

    for(i in 1 until csv.size){
        val data = processarDataSemBarra(csv[i][colunaData], calendar, i, anoMin = 1900)
        csv[i][colunaDataEditada] = data

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

fun processarData(data: String, calendar: Calendar, index: Int, anoMin: Int = 0, sep: String = "/"): String {
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
                            if(dataValor < dataAtual){
                                return setupDateString(valores[2])+setupDateString(valores[1])+setupDateString(valores[0])
                            }
                        }
                    }
                }
            }
        }
    }

    println("$index - Data errada: $data")

    return NULL
}

fun processarDataSemBarra(data: String, calendar: Calendar, index: Int, anoMin: Int = 0): String {

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
                val resultadoDoisDig = processarData(dataComDoisDigDia, calendar, index, anoMin)
                if(resultadoDoisDig != NULL){
                    return resultadoDoisDig
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

    return NULL
}