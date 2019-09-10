import java.io.File
import java.util.*
import java.util.Calendar.*
import kotlin.text.Charsets.ISO_8859_1

const val NULL = "NULL"
const val path = "C:\\Users\\Henrique\\Documents\\Unifor\\Ciência de Dados\\"

const val minIdadeAlunos = 0
const val maxIdadeAlunos = 57

const val minIdadeDengue = 0
const val maxIdadeDengue = 127

const val minIdadeOnibus = 0
const val maxIdadeOnibus = 126

fun main(){
    baseAlunos()
    baseDengue()
    baseOnibus()
}

fun baseAlunos(){
    val csv = readCSV("${path}Base de Alunos3.csv")

    val colunaNome = getColumnIndex(csv, "Nome")
    val colunaNomeEditado = addNewColumn(csv, "nomeEditado")
    val colunaNomePai = getColumnIndex(csv, "Nome do Pai")
    val colunaNomePaiEditado = addNewColumn(csv, "nomeDoPaiEditado")
    val colunaNomeMae = getColumnIndex(csv, "Nome da Mae")
    val colunaNomeMaeEditado = addNewColumn(csv, "nomeDaMaeEditado")

    val colunaSexo = getColumnIndex(csv, "Sexo")
    val colunaSexoEditado = addNewColumn(csv, "sexoEditado")

    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaDataEditada = addNewColumn(csv, "dataEditada")
    val colunaIdade = addNewColumn(csv, "idade")

    val calendar = Calendar.getInstance()

    var linhasSemData = 0

    for(i in 1 until csv.size){
        val nome = csv[i][colunaNome]
        csv[i][colunaNomeEditado] = nome.padronizarNome()

        val nomePai = csv[i][colunaNomePai]
        csv[i][colunaNomePaiEditado] = nomePai.padronizarNome()

        val nomeMae = csv[i][colunaNomeMae]
        csv[i][colunaNomeMaeEditado] = nomeMae.padronizarNome()

        csv[i][colunaSexoEditado] = csv[i][colunaSexo].toUpperCase()

        val (data, idade) = processarData(csv[i][colunaData], calendar, i, sep = "-")
        csv[i][colunaDataEditada] = data
        csv[i][colunaIdade] = idade.toString()

        if(idade in minIdadeAlunos..maxIdadeAlunos){
            csv[i][colunaDataEditada] = data
            csv[i][colunaIdade] = idade.toString()
        }else{
            csv[i][colunaDataEditada] = ""
            csv[i][colunaIdade] = ""
        }

        if(data == NULL){
            linhasSemData++
        }
    }

    println("Linhas sem data: $linhasSemData")

    val csvComBairrosPareados = parearBairros(csv, "${path}bairros.txt")

    writeCSV("${path}Base de Alunos3 Editado.csv", csvComBairrosPareados)
}

fun baseDengue(){
    val csv = readCSV("${path}Base de Dengue3.csv")

    val colunaNome = getColumnIndex(csv, "Nome")
    val colunaNomeEditado = addNewColumn(csv, "nomeEditado")
    val colunaNomePai = getColumnIndex(csv, "Nome do Pai")
    val colunaNomePaiEditado = addNewColumn(csv, "nomeDoPaiEditado")
    val colunaNomeMae = getColumnIndex(csv, "Nome da Mae")
    val colunaNomeMaeEditado = addNewColumn(csv, "nomeDaMaeEditado")

    val colunaSexo = getColumnIndex(csv, "Sexo")
    val colunaSexoEditado = addNewColumn(csv, "sexoEditado")

    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaDataEditada = addNewColumn(csv, "dataEditada")
    val colunaIdade = addNewColumn(csv, "idade")
    val colunaDataDengue = getColumnIndex(csv, "Data da Dengue")
    val colunaDataDengueEditada = addNewColumn(csv, "dataDengueEditada")

    val calendar = Calendar.getInstance()

    var linhasSemData = 0
    var linhasDengueSemData = 0

    for(i in 1 until csv.size){
        val nome = csv[i][colunaNome]
        csv[i][colunaNomeEditado] = nome.padronizarNome()

        val nomePai = csv[i][colunaNomePai]
        csv[i][colunaNomePaiEditado] = nomePai.padronizarNome()

        val nomeMae = csv[i][colunaNomeMae]
        csv[i][colunaNomeMaeEditado] = nomeMae.padronizarNome()

        csv[i][colunaSexoEditado] = csv[i][colunaSexo].toUpperCase()

        val (data, idade) = processarData(csv[i][colunaData], calendar, i, sep = "/")
        csv[i][colunaDataEditada] = data
        csv[i][colunaIdade] = idade.toString()

        if(idade in minIdadeDengue..maxIdadeDengue){
            csv[i][colunaDataEditada] = data
            csv[i][colunaIdade] = idade.toString()
        }else{
            csv[i][colunaDataEditada] = ""
            csv[i][colunaIdade] = ""
        }

        val (dataDengue, _) = processarData(csv[i][colunaDataDengue], calendar, i, sep = "/")

        if((data == NULL && dataDengue != NULL) || (data != NULL && dataDengue != NULL && dataDengue.toInt() > data.toInt())){
            csv[i][colunaDataDengueEditada] = dataDengue
        }else{
            csv[i][colunaDataDengueEditada] = ""
        }

        if(data == NULL){
            linhasSemData++
        }
        if(dataDengue == NULL){
            linhasDengueSemData++
        }
    }

    println("Linhas sem data de nascimento: $linhasSemData")
    println("Linhas sem data da dengue: $linhasDengueSemData")

    val csvComBairrosPareados = parearBairros(csv, "${path}bairros.txt")

    writeCSV("${path}Base de Dengue3 Editado.csv", csvComBairrosPareados)
}

fun baseOnibus(){
    val csv = readCSV("${path}Base de Onibus3.csv")
    csv.igualarLinhas()

    val colunaNome = getColumnIndex(csv, "Nome")
    val colunaNomeEditado = addNewColumn(csv, "nomeEditado")
    val colunaNomePai = getColumnIndex(csv, "Nome do Pai")
    val colunaNomePaiEditado = addNewColumn(csv, "nomeDoPaiEditado")
    val colunaNomeMae = getColumnIndex(csv, "Nome da Mae")
    val colunaNomeMaeEditado = addNewColumn(csv, "nomeDaMaeEditado")

    val colunaSexo = getColumnIndex(csv, "Sexo")
    val colunaSexoEditado = addNewColumn(csv, "sexoEditado")

    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaDataEditada = addNewColumn(csv, "dataEditada")
    val colunaIdade = addNewColumn(csv, "idade")

    val calendar = Calendar.getInstance()

    var linhasSemData = 0

    for(i in 1 until csv.size){
        val nome = csv[i][colunaNome]
        csv[i][colunaNomeEditado] = nome.padronizarNome()

        val nomePai = csv[i][colunaNomePai]
        csv[i][colunaNomePaiEditado] = nomePai.padronizarNome()

        val nomeMae = csv[i][colunaNomeMae]
        csv[i][colunaNomeMaeEditado] = nomeMae.padronizarNome()

        csv[i][colunaSexoEditado] = if(csv[i][colunaSexo].toUpperCase() == "H") "M" else "F"

        val (data, idade) = processarDataSemBarra(csv[i][colunaData], calendar, i)

        if(idade in minIdadeOnibus..maxIdadeOnibus){
            csv[i][colunaDataEditada] = data
            csv[i][colunaIdade] = idade.toString()
        }else{
            csv[i][colunaDataEditada] = ""
            csv[i][colunaIdade] = ""
        }

        if(data == NULL){
            linhasSemData++
        }
    }

    println("Linhas sem data: $linhasSemData")

    val csvComBairrosPareados = parearBairros(csv, "${path}bairros.txt")

    writeCSV("${path}Base de Onibus3 Editado.csv", csvComBairrosPareados)
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

fun parearBairros(csv: MutableList<MutableList<String>>, bairrosListPath: String): MutableList<MutableList<String>> {
    val colunaBairro = getColumnIndex(csv, "Bairro")
    if(colunaBairro > -1){
        val bairros = readTXTList(bairrosListPath)
        val colunaPorcSimilar = addNewColumn(csv, "porcBairro")
        val colunaBairroPareado = addNewColumn(csv, "bairroPareado")
        val colunaIDBairroPareado = addNewColumn(csv, "idBairro")

        var menorPorcentagem = Double.MAX_VALUE
        var nomeBairro = ""
        var nomeMaisParecido = ""

        for(i in 1 until csv.size){
            val bairroAtual = csv[i][colunaBairro]

            var maiorPorc = 0.0
            var bairroMaisParecido = NULL
            var idBairroMaisParecido = -1

            bairros.forEachIndexed { index, bairro ->
                val porc = levenshteinPercentage(bairro, bairroAtual)
                if(porc > maiorPorc){
                    maiorPorc = porc
                    bairroMaisParecido = bairro
                    idBairroMaisParecido = index
                }
            }

            if(maiorPorc < menorPorcentagem){
                menorPorcentagem = maiorPorc
                nomeBairro = bairroAtual
                nomeMaisParecido = bairroMaisParecido
            }

            csv[i][colunaPorcSimilar] = maiorPorc.toString()
            csv[i][colunaBairroPareado] = bairroMaisParecido
            csv[i][colunaIDBairroPareado] = idBairroMaisParecido.toString()
        }

        println("Menor porcentagem: $menorPorcentagem - Nome: $nomeBairro, Mais parecido: $nomeMaisParecido")
    }

    return csv
}

fun parearBairros(bairrosListPath: String, sourcePath: String, endPath: String){
    writeCSV(endPath, parearBairros(readCSV(sourcePath), bairrosListPath))
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