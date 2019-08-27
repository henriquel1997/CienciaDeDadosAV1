import java.io.File
import java.util.*
import kotlin.text.Charsets.ISO_8859_1

const val NULL = "NULL"

fun main(){
    val csv = readCSV("C:\\Users\\Henrique\\Documents\\Unifor\\Ciência de Dados\\Base de Dengue3 Antigo.csv")
    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaDataDengue = getColumnIndex(csv, "Data da Dengue")


    val calendar = Calendar.getInstance()

    var linhasSemData = 0

    for(i in 1 until csv.size){
        val data = processarData(csv[i][colunaData], calendar, i)
        //val data = processarDataSemBarra(csv[i][colunaData], calendar, i)
        csv[i][colunaData] = data

        val dataDengue = processarData(csv[i][colunaDataDengue], calendar, i)
        csv[i][colunaDataDengue] = dataDengue

        if(data == NULL){
            linhasSemData++
        }
    }

    println("Linhas sem data: $linhasSemData")

    writeCSV("C:\\Users\\Henrique\\Documents\\Unifor\\Ciência de Dados\\Base de Dengue3.csv", csv)
}

fun readCSV(filepath: String, sep: String = ";"): MutableList<MutableList<String>> {
    val linhas = mutableListOf<MutableList<String>>()

    for(linhasTexto in File(filepath).readLines(ISO_8859_1)){
        linhas.add(linhasTexto.split(sep).toMutableList())
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

fun setupDateString(string: String): String {
    if(string.length < 2){
        return "0$string"
    }
    return string
}

fun processarData(data: String, calendar: Calendar, index: Int): String {
    val valores = data.replace("\\s".toRegex(), "").split("/")

    if(valores.size == 3){
        valores[0].toIntOrNull()?.let { dia ->
            valores[1].toIntOrNull()?.let { mes ->
                valores[2].toIntOrNull()?.let { ano ->
                    if(dia > 0 && ano > 0 && mes > 0){
                        val dataAtual = calendar.timeInMillis
                        val dataValor = GregorianCalendar(ano, mes - 1, dia).timeInMillis
                        if(dataValor < dataAtual){
                            return setupDateString(valores[0])+setupDateString(valores[1])+setupDateString(valores[2])
                        }
                    }
                }
            }
        }
    }

    println("$index - Data errada: $data")

    return NULL
}

fun processarDataSemBarra(data: String, calendar: Calendar, index: Int): String {

    data.toIntOrNull()?.let { numero ->
        val dataSemZero = numero.toString()
        when(data.length){
            6 -> {
                val dataComBarra = dataSemZero.substring(0, 1) + "/" + dataSemZero.substring(1, 2) + "/" + dataSemZero.substring(2, dataSemZero.length)
                return processarData(dataComBarra, calendar, index)
            }
            7 -> {
                val dataComUltimaBarra = dataSemZero.substring(0, 3) + "/" + dataSemZero.substring(3, dataSemZero.length)
                val dataComDoisDigDia = dataComUltimaBarra.substring(0, 2) + "/" + dataComUltimaBarra.substring(2, dataComUltimaBarra.length)
                val resultadoDoisDig = processarData(dataComDoisDigDia, calendar, index)
                if(resultadoDoisDig != NULL){
                    return resultadoDoisDig
                }

                val dataComUmDigDia = dataComUltimaBarra.substring(0, 1) + "/" + dataComUltimaBarra.substring(1, dataComUltimaBarra.length)
                return processarData(dataComUmDigDia, calendar, index)

            }
            8 -> {
                val dataComBarra = dataSemZero.substring(0, 2) + "/" + dataSemZero.substring(2, 4) + "/" + dataSemZero.substring(4, dataSemZero.length)
                return processarData(dataComBarra, calendar, index)
            }

            else -> {}
        }
    }

    println("$index - Data errada: $data")

    return NULL
}