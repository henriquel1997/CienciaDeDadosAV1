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
    //baseAlunos()
    //baseDengue()
    //baseOnibus()
    parearDuplicatasAlunos()
    parearDuplicatasDengue()
    parearDuplicatasOnibus()
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

fun parearDuplicatasAlunos(){
    val csv = readCSV("${path}Base de Alunos3 Editado.csv")

    val colunaNomeEditado = getColumnIndex(csv, "nomeEditado")
    val colunaNomePaiEditado = getColumnIndex(csv, "nomeDoPaiEditado")
    val colunaNomeMaeEditado = getColumnIndex(csv, "nomeDaMaeEditado")
    val colunaDataEditada = getColumnIndex(csv, "dataEditada")
    val colunaIdade = getColumnIndex(csv, "idade")
    val colunaIdBairro = getColumnIndex(csv, "idBairro")
    val colunaSexoEditado = getColumnIndex(csv, "sexoEditado")

    val concatenarTudo = { linha: MutableList<String> ->
        "${linha[colunaNomeEditado]}${linha[colunaNomePaiEditado]}${linha[colunaNomeMaeEditado]}${linha[colunaNomeMaeEditado]}${linha[colunaDataEditada]}${linha[colunaIdade]}${linha[colunaIdBairro]}${linha[colunaSexoEditado]}"
    }

    val concatenarNomes = { linha: MutableList<String> ->
        "${linha[colunaNomeEditado]}${linha[colunaNomePaiEditado]}${linha[colunaNomeMaeEditado]}${linha[colunaNomeMaeEditado]}"
    }

    println("Checando Duplicatas Alunos:")

    println("Tudo concatenado:")
    parearDuplicados(csv, "idDuplicadoTudo", "porcSimilarDuplicadoTudo"){ linha1, linha2 ->
        val linha1Conc = concatenarTudo(linha1)
        val linha2Conc = concatenarTudo(linha2)

        jaroWinklerSimilarity(linha1Conc, linha2Conc)
    }

    println("Nomes concatenado:")
    parearDuplicados(csv, "idDuplicadoNomes", "porcSimilarDuplicadoNomes"){ linha1, linha2 ->
        val linha1Conc = concatenarNomes(linha1)
        val linha2Conc = concatenarNomes(linha2)

        jaroWinklerSimilarity(linha1Conc, linha2Conc)
    }

    println("Apenas nome da pessoa:")
    parearDuplicados(csv, "idDuplicadoNomePessoa", "porcSimilarDuplicadoNomePessoa"){ linha1, linha2 ->
        jaroWinklerSimilarity(linha1[colunaNomeEditado], linha2[colunaNomeEditado])
    }

    writeCSV("${path}Base de Alunos3 Pareado.csv", csv)
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

fun parearDuplicatasDengue(){
    val csv = readCSV("${path}Base de Dengue3 Editado.csv")

    val colunaNomeEditado = getColumnIndex(csv, "nomeEditado")
    val colunaNomePaiEditado = getColumnIndex(csv, "nomeDoPaiEditado")
    val colunaNomeMaeEditado = getColumnIndex(csv, "nomeDaMaeEditado")
    val colunaDataEditada = getColumnIndex(csv, "dataEditada")
    val colunaIdade = getColumnIndex(csv, "idade")
    val colunaDataDengueEditada = getColumnIndex(csv, "dataDengueEditada")
    val colunaIdBairro = getColumnIndex(csv, "idBairro")
    val colunaSexoEditado = getColumnIndex(csv, "sexoEditado")

    val concatenarTudo = { linha: MutableList<String> ->
        "${linha[colunaNomeEditado]}${linha[colunaNomePaiEditado]}${linha[colunaNomeMaeEditado]}${linha[colunaNomeMaeEditado]}${linha[colunaDataEditada]}${linha[colunaIdade]}${linha[colunaDataDengueEditada]}${linha[colunaIdBairro]}${linha[colunaSexoEditado]}"
    }

    val concatenarNomes = { linha: MutableList<String> ->
        "${linha[colunaNomeEditado]}${linha[colunaNomePaiEditado]}${linha[colunaNomeMaeEditado]}${linha[colunaNomeMaeEditado]}"
    }

    println("Checando Duplicatas Dengue:")

    println("Tudo concatenado:")
    parearDuplicados(csv, "idDuplicadoTudo", "porcSimilarDuplicadoTudo"){ linha1, linha2 ->
        val linha1Conc = concatenarTudo(linha1)
        val linha2Conc = concatenarTudo(linha2)

        jaroWinklerSimilarity(linha1Conc, linha2Conc)
    }

    println("Nomes concatenado:")
    parearDuplicados(csv, "idDuplicadoNomes", "porcSimilarDuplicadoNomes"){ linha1, linha2 ->
        val linha1Conc = concatenarNomes(linha1)
        val linha2Conc = concatenarNomes(linha2)

        jaroWinklerSimilarity(linha1Conc, linha2Conc)
    }

    println("Apenas nome da pessoa:")
    parearDuplicados(csv, "idDuplicadoNomePessoa", "porcSimilarDuplicadoNomePessoa"){ linha1, linha2 ->
        jaroWinklerSimilarity(linha1[colunaNomeEditado], linha2[colunaNomeEditado])
    }

    writeCSV("${path}Base de Dengue3 Pareado.csv", csv)
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

fun parearDuplicatasOnibus(){
    val csv = readCSV("${path}Base de Onibus3 Editado.csv")

    val colunaNomeEditado = getColumnIndex(csv, "nomeEditado")
    val colunaNomePaiEditado = getColumnIndex(csv, "nomeDoPaiEditado")
    val colunaNomeMaeEditado = getColumnIndex(csv, "nomeDaMaeEditado")
    val colunaDataEditada = getColumnIndex(csv, "dataEditada")
    val colunaIdade = getColumnIndex(csv, "idade")
    val colunaIdBairro = getColumnIndex(csv, "idBairro")
    val colunaSexoEditado = getColumnIndex(csv, "sexoEditado")

    val concatenarTudo = { linha: MutableList<String> ->
        "${linha[colunaNomeEditado]}${linha[colunaNomePaiEditado]}${linha[colunaNomeMaeEditado]}${linha[colunaNomeMaeEditado]}${linha[colunaDataEditada]}${linha[colunaIdade]}${linha[colunaIdBairro]}${linha[colunaSexoEditado]}"
    }

    val concatenarNomes = { linha: MutableList<String> ->
        "${linha[colunaNomeEditado]}${linha[colunaNomePaiEditado]}${linha[colunaNomeMaeEditado]}${linha[colunaNomeMaeEditado]}"
    }

    println("Checando Duplicatas Onibus:")

    println("Tudo concatenado:")
    parearDuplicados(csv, "idDuplicadoTudo", "porcSimilarDuplicadoTudo"){ linha1, linha2 ->
        val linha1Conc = concatenarTudo(linha1)
        val linha2Conc = concatenarTudo(linha2)

        jaroWinklerSimilarity(linha1Conc, linha2Conc)
    }

    println("Nomes concatenado:")
    parearDuplicados(csv, "idDuplicadoNomes", "porcSimilarDuplicadoNomes"){ linha1, linha2 ->
        val linha1Conc = concatenarNomes(linha1)
        val linha2Conc = concatenarNomes(linha2)

        jaroWinklerSimilarity(linha1Conc, linha2Conc)
    }

    println("Apenas nome da pessoa:")
    parearDuplicados(csv, "idDuplicadoNomePessoa", "porcSimilarDuplicadoNomePessoa"){ linha1, linha2 ->
        jaroWinklerSimilarity(linha1[colunaNomeEditado], linha2[colunaNomeEditado])
    }

    writeCSV("${path}Base de Onibus3 Pareado.csv", csv)
}