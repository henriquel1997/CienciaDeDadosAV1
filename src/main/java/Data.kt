import java.util.*

fun setupDateString(string: String): String {
    if(string.length < 2){
        return "0$string"
    }
    return string
}

fun getYearDiff(a: Calendar, b: Calendar): Int {
    var diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR)

    if(a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || (a.get(Calendar.MONTH) == b.get(Calendar.MONTH)) && (a.get(
            Calendar.DATE
        ) > b.get(Calendar.DATE))){
        diff--
    }

    return diff
}

fun processarData(data: String, calendar: Calendar, anoMin: Int = 0, sep: String = "/"): Pair<String, Int> {
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

    //println("$index - Data errada: $data")

    return Pair(NULL, Integer.MIN_VALUE)
}

fun processarDataSemBarra(data: String, calendar: Calendar, anoMin: Int = 0): Pair<String, Int> {

    data.toIntOrNull()?.let { numero ->
        val dataSemZero = numero.toString()
        when(dataSemZero.length){
            6 -> {
                val dataComBarra = dataSemZero.substring(0, 1) + "/" + dataSemZero.substring(1, 2) + "/" + dataSemZero.substring(2, dataSemZero.length)
                return processarData(dataComBarra, calendar, anoMin)
            }
            7 -> {
                val dataComUltimaBarra = dataSemZero.substring(0, 3) + "/" + dataSemZero.substring(3, dataSemZero.length)
                val dataComDoisDigDia = dataComUltimaBarra.substring(0, 2) + "/" + dataComUltimaBarra.substring(2, dataComUltimaBarra.length)
                val parDoisDig = processarData(dataComDoisDigDia, calendar, anoMin)
                if(parDoisDig.first != NULL){
                    return parDoisDig
                }

                val dataComUmDigDia = dataComUltimaBarra.substring(0, 1) + "/" + dataComUltimaBarra.substring(1, dataComUltimaBarra.length)
                return processarData(dataComUmDigDia, calendar, anoMin)

            }
            8 -> {
                val dataComBarra = dataSemZero.substring(0, 2) + "/" + dataSemZero.substring(2, 4) + "/" + dataSemZero.substring(4, dataSemZero.length)
                return processarData(dataComBarra, calendar, anoMin)
            }

            else -> {}
        }
    }

    //println("$index - Data errada: $data")

    return Pair(NULL, Integer.MIN_VALUE)
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