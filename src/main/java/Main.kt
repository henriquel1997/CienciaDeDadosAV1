import java.util.*

const val NULL = "NULL"
const val path = "C:\\Users\\Henrique\\Documents\\Unifor\\Ciência de Dados\\"

const val minIdadeAlunos = 0
const val maxIdadeAlunos = 57

const val minIdadeDengue = 0
const val maxIdadeDengue = 127

const val minIdadeOnibus = 0
const val maxIdadeOnibus = 126

fun main(){
//    baseAlunos()
//    baseDengue()
//    baseOnibus()
    parearDuplicatasAlunos()
    parearDuplicatasDengue()
    parearDuplicatasOnibus()
}

fun baseAlunos(){
    val csv = readCSV("${path}Base de Alunos3.csv")

    val colunaNome = getColumnIndex(csv, "Nome")
    val colunaNomePai = getColumnIndex(csv, "Nome do Pai")
    val colunaNomeMae = getColumnIndex(csv, "Nome da Mae")

    val colunaSexo = getColumnIndex(csv, "Sexo")

    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaIdade = addNewColumn(csv, "idade")

    val calendar = Calendar.getInstance()

    var linhasSemData = 0

    for(i in 1 until csv.size){
        csv[i][colunaNome] = csv[i][colunaNome].padronizarNome()

        csv[i][colunaNomePai] = csv[i][colunaNomePai].padronizarNome()

        csv[i][colunaNomeMae] = csv[i][colunaNomeMae].padronizarNome()

        csv[i][colunaSexo] = csv[i][colunaSexo].toUpperCase()

        val (data, idade) = processarData(csv[i][colunaData], calendar, i, sep = "-")
        csv[i][colunaData] = data
        csv[i][colunaIdade] = idade.toString()

        if(idade in minIdadeAlunos..maxIdadeAlunos){
            csv[i][colunaData] = data
            csv[i][colunaIdade] = idade.toString()
        }else{
            csv[i][colunaData] = ""
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

fun parearDuplicatasAlunos(){
    val csv = readCSV("${path}Base de Alunos3 Editado.csv")

    val colunaNome = getColumnIndex(csv, "Nome")
    val colunaNomePai = getColumnIndex(csv, "Nome do Pai")
    val colunaNomeMae = getColumnIndex(csv, "Nome da Mae")
    val colunaSexo = getColumnIndex(csv, "Sexo")
    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaIdade = getColumnIndex(csv, "idade")
    val colunaIdBairro = getColumnIndex(csv, "idBairro")

    val jaroWrinklerOf = { linha1: MutableList<String> , linha2: MutableList<String>, coluna: Int  ->
        jaroWinklerSimilarity(linha1[coluna], linha2[coluna])
    }

    val concatenarTudo = { linha: MutableList<String> ->
        "${linha[colunaNome]}${linha[colunaNomePai]}${linha[colunaNomeMae]}${linha[colunaData]}${linha[colunaIdade]}${linha[colunaIdBairro]}${linha[colunaSexo]}"
    }

    val concatenarNomes = { linha: MutableList<String> ->
        "${linha[colunaNome]}${linha[colunaNomePai]}${linha[colunaNomeMae]}"
    }

    println("Checando Duplicatas Alunos:")

    println("Tudo concatenado:")
    criarComparacaoPareamento(csv){ linha1, linha2 ->
        //        val linha1Conc = concatenarNomes(linha1)
//        val linha2Conc = concatenarNomes(linha2)
//
//        jaroWinklerSimilarity(linha1Conc, linha2Conc)

        //(jaroWinklerSimilarity(linha1[colunaNome], linha2[colunaNome]) + jaroWinklerSimilarity(linha1[colunaNomePai], linha2[colunaNomePai]) + jaroWinklerSimilarity(linha1[colunaNomeMae], linha2[colunaNomeMae])) / 3.0
        (jaroWrinklerOf(linha1, linha2, colunaNome) + jaroWrinklerOf(linha1, linha2, colunaNomePai) + jaroWrinklerOf(linha1, linha2, colunaNomeMae) + jaroWrinklerOf(linha1, linha2, colunaData) + (linha1[colunaIdade] == linha2[colunaIdade]).toDouble() + (linha1[colunaIdBairro] == linha2[colunaIdBairro]).toDouble() + (linha1[colunaSexo] == linha2[colunaSexo]).toDouble()) / 7.0
    }

    writeCSV("${path}Base de Alunos3 Comparacao Media Tudo.csv", csv)
}

fun baseDengue(){
    val csv = readCSV("${path}Base de Dengue3.csv")

    val colunaNome = getColumnIndex(csv, "Nome")
    val colunaNomePai = getColumnIndex(csv, "Nome do Pai")
    val colunaNomeMae = getColumnIndex(csv, "Nome da Mae")

    val colunaSexo = getColumnIndex(csv, "Sexo")

    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaIdade = addNewColumn(csv, "idade")

    val colunaDataDengue = getColumnIndex(csv, "Data da Dengue")

    val calendar = Calendar.getInstance()

    var linhasSemData = 0
    var linhasDengueSemData = 0

    for(i in 1 until csv.size){
        val nome = csv[i][colunaNome]
        csv[i][colunaNome] = nome.padronizarNome()

        val nomePai = csv[i][colunaNomePai]
        csv[i][colunaNomePai] = nomePai.padronizarNome()

        val nomeMae = csv[i][colunaNomeMae]
        csv[i][colunaNomeMae] = nomeMae.padronizarNome()

        csv[i][colunaSexo] = csv[i][colunaSexo].toUpperCase()

        val (data, idade) = processarData(csv[i][colunaData], calendar, i, sep = "/")
        csv[i][colunaData] = data
        csv[i][colunaIdade] = idade.toString()

        if(idade in minIdadeDengue..maxIdadeDengue){
            csv[i][colunaData] = data
            csv[i][colunaIdade] = idade.toString()
        }else{
            csv[i][colunaData] = ""
            csv[i][colunaIdade] = ""
        }

        val (dataDengue, _) = processarData(csv[i][colunaDataDengue], calendar, i, sep = "/")

        if((data == NULL && dataDengue != NULL) || (data != NULL && dataDengue != NULL && dataDengue.toInt() > data.toInt())){
            csv[i][colunaDataDengue] = dataDengue
        }else{
            csv[i][colunaDataDengue] = ""
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

fun parearDuplicatasDengue(){
    val csv = readCSV("${path}Base de Dengue3 Editado.csv")

    val colunaNome = getColumnIndex(csv, "Nome")
    val colunaNomePai = getColumnIndex(csv, "Nome do Pai")
    val colunaNomeMae = getColumnIndex(csv, "Nome da Mae")
    val colunaSexo = getColumnIndex(csv, "Sexo")
    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaIdade = getColumnIndex(csv, "idade")
    val colunaIdBairro = getColumnIndex(csv, "idBairro")
    val colunaDataDengue = getColumnIndex(csv, "Data da Dengue")

    val jaroWrinklerOf = { linha1: MutableList<String> , linha2: MutableList<String>, coluna: Int  ->
        jaroWinklerSimilarity(linha1[coluna], linha2[coluna])
    }

    val concatenarTudo = { linha: MutableList<String> ->
        "${linha[colunaNome]}${linha[colunaNomePai]}${linha[colunaNomeMae]}${linha[colunaData]}${linha[colunaIdade]}${linha[colunaDataDengue]}${linha[colunaIdBairro]}${linha[colunaSexo]}"
    }

    val concatenarNomes = { linha: MutableList<String> ->
        "${linha[colunaNome]}${linha[colunaNomePai]}${linha[colunaNomeMae]}"
    }

    println("Checando Duplicatas Dengue:")

    println("Tudo concatenado:")
    criarComparacaoPareamento(csv){ linha1, linha2 ->
        //        val linha1Conc = concatenarNomes(linha1)
//        val linha2Conc = concatenarNomes(linha2)
//
//        jaroWinklerSimilarity(linha1Conc, linha2Conc)

        //(jaroWinklerSimilarity(linha1[colunaNome], linha2[colunaNome]) + jaroWinklerSimilarity(linha1[colunaNomePai], linha2[colunaNomePai]) + jaroWinklerSimilarity(linha1[colunaNomeMae], linha2[colunaNomeMae])) / 3.0
        (jaroWrinklerOf(linha1, linha2, colunaNome) + jaroWrinklerOf(linha1, linha2, colunaNomePai) + jaroWrinklerOf(linha1, linha2, colunaNomeMae) + jaroWrinklerOf(linha1, linha2, colunaData) + (linha1[colunaIdade] == linha2[colunaIdade]).toDouble() + (linha1[colunaIdBairro] == linha2[colunaIdBairro]).toDouble() + (linha1[colunaSexo] == linha2[colunaSexo]).toDouble()) / 7.0
    }

    writeCSV("${path}Base de Dengue3 Comparacao Media Tudo.csv", csv)
}

fun baseOnibus(){
    val csv = readCSV("${path}Base de Onibus3.csv")
    csv.igualarLinhas()

    val colunaNome = getColumnIndex(csv, "Nome")
    val colunaNomePai = getColumnIndex(csv, "Nome do Pai")
    val colunaNomeMae = getColumnIndex(csv, "Nome da Mae")

    val colunaSexo = getColumnIndex(csv, "Sexo")

    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaIdade = addNewColumn(csv, "idade")

    val calendar = Calendar.getInstance()

    var linhasSemData = 0

    for(i in 1 until csv.size){
        val nome = csv[i][colunaNome]
        csv[i][colunaNome] = nome.padronizarNome()

        val nomePai = csv[i][colunaNomePai]
        csv[i][colunaNomePai] = nomePai.padronizarNome()

        val nomeMae = csv[i][colunaNomeMae]
        csv[i][colunaNomeMae] = nomeMae.padronizarNome()

        csv[i][colunaSexo] = if(csv[i][colunaSexo].toUpperCase() == "H") "M" else "F"

        val (data, idade) = processarDataSemBarra(csv[i][colunaData], calendar, i)

        if(idade in minIdadeOnibus..maxIdadeOnibus){
            csv[i][colunaData] = data
            csv[i][colunaIdade] = idade.toString()
        }else{
            csv[i][colunaData] = ""
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

fun parearDuplicatasOnibus(){
    val csv = readCSV("${path}Base de Onibus3 Editado.csv")

    val colunaNome = getColumnIndex(csv, "Nome")
    val colunaNomePai = getColumnIndex(csv, "Nome do Pai")
    val colunaNomeMae = getColumnIndex(csv, "Nome da Mae")
    val colunaSexo = getColumnIndex(csv, "Sexo")
    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaIdade = getColumnIndex(csv, "idade")
    val colunaIdBairro = getColumnIndex(csv, "idBairro")

    val jaroWrinklerOf = { linha1: MutableList<String> , linha2: MutableList<String>, coluna: Int  ->
        jaroWinklerSimilarity(linha1[coluna], linha2[coluna])
    }

    val concatenarTudo = { linha: MutableList<String> ->
        "${linha[colunaNome]}${linha[colunaNomePai]}${linha[colunaNomeMae]}${linha[colunaData]}${linha[colunaIdade]}${linha[colunaIdBairro]}${linha[colunaSexo]}"
    }

    val concatenarNomes = { linha: MutableList<String> ->
        "${linha[colunaNome]}${linha[colunaNomePai]}${linha[colunaNomeMae]}"
    }

    println("Checando Duplicatas Onibus:")

    println("Tudo concatenado:")
    criarComparacaoPareamento(csv){ linha1, linha2 ->
        //        val linha1Conc = concatenarNomes(linha1)
//        val linha2Conc = concatenarNomes(linha2)
//
//        jaroWinklerSimilarity(linha1Conc, linha2Conc)

        //(jaroWinklerSimilarity(linha1[colunaNome], linha2[colunaNome]) + jaroWinklerSimilarity(linha1[colunaNomePai], linha2[colunaNomePai]) + jaroWinklerSimilarity(linha1[colunaNomeMae], linha2[colunaNomeMae])) / 3.0
        (jaroWrinklerOf(linha1, linha2, colunaNome) + jaroWrinklerOf(linha1, linha2, colunaNomePai) + jaroWrinklerOf(linha1, linha2, colunaNomeMae) + jaroWrinklerOf(linha1, linha2, colunaData) + (linha1[colunaIdade] == linha2[colunaIdade]).toDouble() + (linha1[colunaIdBairro] == linha2[colunaIdBairro]).toDouble() + (linha1[colunaSexo] == linha2[colunaSexo]).toDouble()) / 7.0
    }

    writeCSV("${path}Base de Onibus3 Comparacao Media Tudo.csv", csv)
}

fun Boolean.toDouble(): Double {
    return if(this){
        1.0
    }else{
        0.0
    }
}