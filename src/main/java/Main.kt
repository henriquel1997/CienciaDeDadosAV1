import TipoComparacao.*
import java.util.*

const val NULL = "NULL"
const val path = "C:\\Users\\Henrique\\Documents\\Unifor\\Ciência de Dados\\" // Windows
//const val path = "/Users/henriquedelima/Downloads/Equipe4/" // Mac

const val minIdadeAlunos = 0
const val maxIdadeAlunos = 57

const val minIdadeDengue = 0
const val maxIdadeDengue = 127

const val minIdadeOnibus = 0
const val maxIdadeOnibus = 126

const val pontoCortePareamento = 0.89 //Jaro Winkler
//const val pontoCortePareamento = 0.77 //Levenshtein Aluno
//const val pontoCortePareamento = 0.72 //Levenshtein Dengue
//const val pontoCortePareamento = 0.703 //Levenshtein Onibus

fun main(){
    baseAlunos()
    baseDengue()
    baseOnibus()
//    parearDuplicatasAlunos()
//    parearDuplicatasDengue()
//    parearDuplicatasOnibus()
//    compararDuplicatas("${path}Base de Alunos3 Editado.csv", "${path}Base de Alunos3 Comparacao JaroW.csv")
//    compararDuplicatas("${path}Base de Dengue3 Editado.csv", "${path}Base de Dengue3 Comparacao JaroW.csv")
//    compararDuplicatas("${path}Base de Onibus3 Editado.csv", "${path}Base de Onibus3 Comparacao JaroW.csv")
}

enum class TipoComparacao{
    JAROW, LEVEN, EQUALS
}

data class ColunasDuplicado(
    val nomeColunaOriginal: String,
    val nomeColunaDuplicado: String,
    val nomeColunaPorc: String,
    val algoritmo: TipoComparacao
)

fun pontuarDuplicados(nomeCSVInput: String, nomeCSVOutput: String){
    val cd = listOf(ColunasDuplicado("", "", "", JAROW))
}

fun pontuarDuplicados(cd: List<ColunasDuplicado>, nomeCSVInput: String, nomeCSVOutput: String){
    val csv = readCSV("$path$nomeCSVInput")

    val mapNovasCol = hashMapOf<String, Int>()

    for(info in cd){
        mapNovasCol[info.nomeColunaPorc] = addNewColumn(csv, info.nomeColunaPorc)
    }

    for(i in 1 until csv.size){
        for(info in cd){
            val original = csv[i][getColumnIndex(csv, info.nomeColunaOriginal)]
            val duplicado = csv[i][getColumnIndex(csv, info.nomeColunaDuplicado)]
            val porcentagem = when(info.algoritmo){
                JAROW -> {
                    jaroWinklerSimilarity(original, duplicado)
                }
                LEVEN -> {
                    levenshteinPercentage(original, duplicado)
                }
                EQUALS -> {
                    if(original == (duplicado)){
                        1.0
                    }else{
                        0.0
                    }
                }
            }

            mapNovasCol[info.nomeColunaPorc]?.let { pos ->
                csv[i][pos] = porcentagem.toString()
            }
        }
    }

    writeCSV("$path$nomeCSVOutput", csv)
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
    val colunaBairro = getColumnIndex(csv, "Bairro")

    val concatenarTudo = { linha: MutableList<String> ->
        "${linha[colunaNome]}${linha[colunaNomePai]}${linha[colunaNomeMae]}${linha[colunaData]}${linha[colunaBairro]}${linha[colunaIdade]}${linha[colunaSexo]}"
    }

    val contarVazios = { linha: MutableList<String> ->
        var cont = 0
        for(param in linha){
            if(param.isEmpty())
                cont++
        }
        cont
    }

    println("Removendo Duplicatas Alunos:")
    removerDuplicados(csv, pontoCortePareamento,
        comparador = { linha1, linha2 ->
            jaroWinklerSimilarity(concatenarTudo(linha1), concatenarTudo(linha2))
        },

        comparadorMaisCompleto = { linha, linhaMaisCompleta ->
            contarVazios(linha) < contarVazios(linhaMaisCompleta)
        }
    )


    writeCSV("${path}Base de Alunos3 Sem Duplicados.csv", csv)
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
    val colunaBairro = getColumnIndex(csv, "Bairro")
    val colunaDataDengue = getColumnIndex(csv, "Data da Dengue")

    val concatenarTudo = { linha: MutableList<String> ->
        "${linha[colunaNome]}${linha[colunaNomePai]}${linha[colunaNomeMae]}${linha[colunaData]}${linha[colunaBairro]}${linha[colunaIdade]}${linha[colunaDataDengue]}${linha[colunaSexo]}"
    }

    val contarVazios = { linha: MutableList<String> ->
        var cont = 0
        for(param in linha){
            if(param.isEmpty())
                cont++
        }
        cont
    }

    println("Removendo Duplicatas Dengue:")
    removerDuplicados(csv, pontoCortePareamento,
        comparador = { linha1, linha2 ->
            jaroWinklerSimilarity(concatenarTudo(linha1), concatenarTudo(linha2))
        },

        comparadorMaisCompleto = { linha, linhaMaisCompleta ->
            contarVazios(linha) < contarVazios(linhaMaisCompleta)
        }
    )


    writeCSV("${path}Base de Dengue3 Sem Duplicados.csv", csv)
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
    val colunaBairro = getColumnIndex(csv, "Bairro")

    val concatenarTudo = { linha: MutableList<String> ->
        "${linha[colunaNome]}${linha[colunaNomePai]}${linha[colunaNomeMae]}${linha[colunaData]}${linha[colunaBairro]}${linha[colunaIdade]}${linha[colunaSexo]}"
    }

    val contarVazios = { linha: MutableList<String> ->
        var cont = 0
        for(param in linha){
            if(param.isEmpty())
                cont++
        }
        cont
    }

    println("Removendo Duplicatas Onibus:")
    removerDuplicados(csv, pontoCortePareamento,
        comparador = { linha1, linha2 ->
            jaroWinklerSimilarity(concatenarTudo(linha1), concatenarTudo(linha2))
        },

        comparadorMaisCompleto = { linha, linhaMaisCompleta ->
            contarVazios(linha) < contarVazios(linhaMaisCompleta)
        }
    )


    writeCSV("${path}Base de Onibus3 Sem Duplicados.csv", csv)
}

fun Boolean.toDouble(): Double {
    return if(this){
        1.0
    }else{
        0.0
    }
}

fun MutableList<MutableList<String>>.copy(): MutableList<MutableList<String>> {
    val newList = mutableListOf<MutableList<String>>()

    for(i in 0 until size){
        newList.add(mutableListOf())
        for(texto in this[i]){
            newList[i].add(texto)
        }
    }

    return newList
}

fun compararDuplicatas(filepath: String, newFilepath: String){
    val csv = readCSV(filepath)

    val colunaNome = getColumnIndex(csv, "Nome")
    val colunaNomePai = getColumnIndex(csv, "Nome do Pai")
    val colunaNomeMae = getColumnIndex(csv, "Nome da Mae")
    val colunaSexo = getColumnIndex(csv, "Sexo")
    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaIdade = getColumnIndex(csv, "idade")
    val colunaIdBairro = getColumnIndex(csv, "idBairro")

//    val jaroWrinklerOf = { linha1: MutableList<String> , linha2: MutableList<String>, coluna: Int  ->
//        jaroWinklerSimilarity(linha1[coluna], linha2[coluna])
//    }

    val concatenarTudo = { linha: MutableList<String> ->
        "${linha[colunaNome]}${linha[colunaNomePai]}${linha[colunaNomeMae]}${linha[colunaData]}${linha[colunaIdade]}${linha[colunaIdBairro]}${linha[colunaSexo]}"
    }

//    val concatenarNomes = { linha: MutableList<String> ->
//        "${linha[colunaNome]}${linha[colunaNomePai]}${linha[colunaNomeMae]}"
//    }

    println("Tudo concatenado:")
    criarComparacaoPareamento(csv){ linha1, linha2 ->
        val linha1Conc = concatenarTudo(linha1)
        val linha2Conc = concatenarTudo(linha2)

        jaroWinklerSimilarity(linha1Conc, linha2Conc)
    }

    writeCSV(newFilepath, csv)
}