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

fun main(){
//    baseAlunos()
//    baseDengue()
//    baseOnibus()
//    parearDuplicatasAlunos()
//    parearDuplicatasDengue()
//    parearDuplicatasOnibus()
//    parearBases()
    juntarCSV()
}

enum class TipoComparacao{
    JAROW, LEVEN, EQUALS
}

data class ColunasDuplicado(
    val posColuna1: Int,
    val algoritmo: TipoComparacao,
    val pontoDeCorte: Double,
    val posColuna2: Int = posColuna1
)

fun calcularPontuacaoDuplicado(cd: List<ColunasDuplicado>, linha1: MutableList<String>, linha2: MutableList<String>): Int {
    var pontuacao = 0
    for(info in cd) {
        val original = linha1[info.posColuna1]
        val duplicado = linha2[info.posColuna2]
        val porcentagem = if (original.isNotEmpty() && duplicado.isNotEmpty()) {
            when (info.algoritmo) {
                JAROW -> {
                    jaroWinklerSimilarity(original, duplicado)
                }
                LEVEN -> {
                    levenshteinPercentage(original, duplicado)
                }
                EQUALS -> {
                    if (original == (duplicado)) {
                        1.0
                    } else {
                        0.0
                    }
                }
            }
        } else {
            0.0
        }

        if(porcentagem >= info.pontoDeCorte){
            pontuacao++
        }
    }

    return pontuacao
}

fun baseAlunos(){
    val csv = readCSV("${path}Base de Alunos3.csv")

    val colunaNome = getColumnIndex(csv, "Nome")
    val colunaNomePai = getColumnIndex(csv, "Nome do Pai")
    val colunaNomeMae = getColumnIndex(csv, "Nome da Mae")

    val colunaSexo = getColumnIndex(csv, "Sexo")

    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaIdade = addNewColumn(csv, "idade", colunaData + 1)

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

    println("Removendo Duplicatas Alunos:")

    val colunaIdBairro = getColumnIndex(csv, "idBairro")
    val colunaBairro = getColumnIndex(csv, "Bairro")

    val cd = listOf(
        ColunasDuplicado(getColumnIndex(csv, "Nome"), JAROW, 0.8),
        ColunasDuplicado(getColumnIndex(csv, "Nome da Mae"), JAROW, 0.8),
        ColunasDuplicado(getColumnIndex(csv, "Nome do Pai"), JAROW, 0.8),
        ColunasDuplicado(getColumnIndex(csv, "Data de Nascimento"), LEVEN, 0.9),
        ColunasDuplicado(colunaIdBairro, EQUALS, 1.0)
    )

    removerDuplicados(csv, 4.0,
        comparador = { linha1, linha2 ->
            //jaroWinklerSimilarity(concatenarTudo(linha1), concatenarTudo(linha2))
            calcularPontuacaoDuplicado(cd, linha1, linha2).toDouble()
        },

        combinadorDeDuplicatas = { duplicatas ->
            val novaLinha = MutableList(duplicatas[0].size) { "" }

            novaLinha[colunaIdBairro] = duplicatas[0][colunaIdBairro]
            novaLinha[colunaBairro] = duplicatas[0][colunaBairro]

            duplicatas.forEach { duplicata ->
                duplicata.forEachIndexed { index, valor ->
                     if(index != colunaIdBairro &&
                        index != colunaBairro &&
                        valor.length > novaLinha[index].length){
                         novaLinha[index] = valor
                     }
                }
            }

            novaLinha
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
    val colunaIdade = addNewColumn(csv, "idade", colunaData + 1)

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

    println("Removendo Duplicatas Dengue:")
    val cd = listOf(
        ColunasDuplicado(getColumnIndex(csv, "Nome"), JAROW, 0.8),
        ColunasDuplicado(getColumnIndex(csv, "Nome da Mae"), JAROW, 0.8),
        ColunasDuplicado(getColumnIndex(csv, "Nome do Pai"), JAROW, 0.8),
        ColunasDuplicado(getColumnIndex(csv, "Data de Nascimento"), LEVEN, 0.9),
        ColunasDuplicado(getColumnIndex(csv, "idBairro"), EQUALS, 1.0)
    )

    removerDuplicados(csv, 4.0,
        comparador = { linha1, linha2 ->
            //jaroWinklerSimilarity(concatenarTudo(linha1), concatenarTudo(linha2))
            calcularPontuacaoDuplicado(cd, linha1, linha2).toDouble()
        },

        combinadorDeDuplicatas = { duplicatas ->
            val novaLinha = MutableList(duplicatas[0].size) { "" }

            duplicatas.forEach { duplicata ->
                duplicata.forEachIndexed { index, valor ->
                    if(valor.length > novaLinha[index].length){
                        novaLinha[index] = valor
                    }
                }
            }

            novaLinha
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

    for(i in 8 until csv[0].size){
        csv[0][i] = (i - 7).toString()
    }

    val colunaData = getColumnIndex(csv, "Data de Nascimento")
    val colunaIdade = addNewColumn(csv, "idade", colunaData + 1)

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

    println("Removendo Duplicatas Onibus:")

    val cd = listOf(
        ColunasDuplicado(getColumnIndex(csv, "Nome"), JAROW, 0.8),
        ColunasDuplicado(getColumnIndex(csv, "Nome da Mae"), JAROW, 0.8),
        ColunasDuplicado(getColumnIndex(csv, "Nome do Pai"), JAROW, 0.8),
        ColunasDuplicado(getColumnIndex(csv, "Data de Nascimento"), LEVEN, 0.9),
        ColunasDuplicado(getColumnIndex(csv, "idBairro"), EQUALS, 1.0)
    )

    removerDuplicados(csv, 4.0,
        comparador = { linha1, linha2 ->
            //jaroWinklerSimilarity(concatenarTudo(linha1), concatenarTudo(linha2))
            calcularPontuacaoDuplicado(cd, linha1, linha2).toDouble()
        },

        combinadorDeDuplicatas = { duplicatas ->
            val novaLinha = MutableList(duplicatas[0].size) { "" }

            duplicatas.forEach { duplicata ->
                duplicata.forEachIndexed { index, valor ->
                    if(valor.length > novaLinha[index].length){
                        novaLinha[index] = valor
                    }
                }
            }

            novaLinha
        }
    )

    writeCSV("${path}Base de Onibus3 Sem Duplicados.csv", csv)
}

fun parearBases(){
    val csvAlunos = readCSV("${path}Base de Alunos3 Sem Duplicados.csv")
    val csvDengue = readCSV("${path}Base de Dengue3 Sem Duplicados.csv")
    val csvOnibus = readCSV("${path}Base de Onibus3 Sem Duplicados.csv")

    println("Pareando Alunos e Dengue:")

    var cd = listOf(
        ColunasDuplicado(getColumnIndex(csvAlunos, "Nome"), JAROW, 0.8, getColumnIndex(csvDengue, "Nome")),
        ColunasDuplicado(getColumnIndex(csvAlunos, "Nome da Mae"), JAROW, 0.8, getColumnIndex(csvDengue, "Nome da Mae")),
        ColunasDuplicado(getColumnIndex(csvAlunos, "Nome do Pai"), JAROW, 0.8, getColumnIndex(csvDengue, "Nome do Pai")),
        ColunasDuplicado(getColumnIndex(csvAlunos, "Data de Nascimento"), LEVEN, 0.9, getColumnIndex(csvDengue, "Data de Nascimento")),
        ColunasDuplicado(getColumnIndex(csvAlunos, "idBairro"), EQUALS, 1.0, getColumnIndex(csvDengue, "idBairro"))
    )

    parearBases(csvAlunos, csvDengue, "idDengue", "idAluno", 4.0){ linha1, linha2 ->
        calcularPontuacaoDuplicado(cd, linha1, linha2).toDouble()
    }

    println("Pareando Alunos e Onibus:")

    cd = listOf(
        ColunasDuplicado(getColumnIndex(csvAlunos, "Nome"), JAROW, 0.8, getColumnIndex(csvOnibus, "Nome")),
        ColunasDuplicado(getColumnIndex(csvAlunos, "Nome da Mae"), JAROW, 0.8, getColumnIndex(csvOnibus, "Nome da Mae")),
        ColunasDuplicado(getColumnIndex(csvAlunos, "Nome do Pai"), JAROW, 0.8, getColumnIndex(csvOnibus, "Nome do Pai")),
        ColunasDuplicado(getColumnIndex(csvAlunos, "Data de Nascimento"), LEVEN, 0.9, getColumnIndex(csvOnibus, "Data de Nascimento")),
        ColunasDuplicado(getColumnIndex(csvAlunos, "idBairro"), EQUALS, 1.0, getColumnIndex(csvOnibus, "idBairro"))
    )

    parearBases(csvAlunos, csvOnibus, "idOnibus", "idAluno", 4.0){ linha1, linha2 ->
        calcularPontuacaoDuplicado(cd, linha1, linha2).toDouble()
    }

    println("Pareando Dengue e Onibus:")

    cd = listOf(
        ColunasDuplicado(getColumnIndex(csvDengue, "Nome"), JAROW, 0.8, getColumnIndex(csvOnibus, "Nome")),
        ColunasDuplicado(getColumnIndex(csvDengue, "Nome da Mae"), JAROW, 0.8, getColumnIndex(csvOnibus, "Nome da Mae")),
        ColunasDuplicado(getColumnIndex(csvDengue, "Nome do Pai"), JAROW, 0.8, getColumnIndex(csvOnibus, "Nome do Pai")),
        ColunasDuplicado(getColumnIndex(csvDengue, "Data de Nascimento"), LEVEN, 0.9, getColumnIndex(csvOnibus, "Data de Nascimento")),
        ColunasDuplicado(getColumnIndex(csvDengue, "idBairro"), EQUALS, 1.0, getColumnIndex(csvOnibus, "idBairro"))
    )

    parearBases(csvDengue, csvOnibus, "idOnibus", "idDengue", 4.0){ linha1, linha2 ->
        calcularPontuacaoDuplicado(cd, linha1, linha2).toDouble()
    }

    writeCSV("${path}Base de Alunos3 Pareado.csv", csvAlunos)
    writeCSV("${path}Base de Dengue3 Pareado.csv", csvDengue)
    writeCSV("${path}Base de Onibus3 Pareado.csv", csvOnibus)
}

fun juntarCSV(){
    val csvAlunos = readCSV("${path}Base de Alunos3 Pareado.csv")
    val csvDengue = readCSV("${path}Base de Dengue3 Pareado.csv")
    val csvOnibus = readCSV("${path}Base de Onibus3 Pareado.csv")

    val ordemColunas = mutableListOf("ID", "Nome", "Nome da Mae", "Nome do Pai", "Sexo", "Data de Nascimento", "idade", "Bairro", "Data da Dengue", "Onibus", "1", "2", "3", "4", "5", "6", "7", "8", "9", "eUmAluno", "teveDengue", "andaDeOnibus")

    val removerColunasDiferentes = { csv: MutableList<MutableList<String>> ->
        var i = 1
        while(i < csv[0].size){
            val tipo = csv[0][i]
            if(tipo != "idAluno" && tipo != "idDengue" && tipo != "idOnibus"){
                if(!ordemColunas.contains(tipo)){
                    removeColumn(csv, tipo)
                    i--
                }
            }
            i++
        }
    }

    val adicionarColunasDeOutrasBases = { csv: MutableList<MutableList<String>> ->
        ordemColunas.forEachIndexed { index,tipo ->
            if(getColumnIndex(csv, tipo) < 0){
                addNewColumn(csv, tipo, index)
            }
        }
    }

    removerColunasDiferentes(csvAlunos)
    removerColunasDiferentes(csvDengue)
    removerColunasDiferentes(csvOnibus)

    adicionarColunasDeOutrasBases(csvAlunos)
    adicionarColunasDeOutrasBases(csvDengue)
    adicionarColunasDeOutrasBases(csvOnibus)

    val baseNova = mutableListOf<MutableList<String>>()
    baseNova.add(ordemColunas)

    val combinadorDeLinhas = { duplicatas: MutableList<MutableList<String>> ->
        val novaLinha = MutableList(duplicatas[0].size) { "" }

        duplicatas.forEach { duplicata ->
            duplicata.forEachIndexed { index, valor ->
                if(valor.length > novaLinha[index].length){
                    novaLinha[index] = valor
                }
            }
        }

        novaLinha
    }

    val juntarTabela = { nomeChave: String, csv: MutableList<MutableList<String>>, outrasTabelas: List<Pair<String, MutableList<MutableList<String>>>> ->

        var eAluno = false
        var teveDengue = false
        var andaDeOnibus = false

        val atualizarFlags = { nome: String ->
            when(nome){
                "idAluno" -> eAluno = true
                "idDengue" -> teveDengue = true
                "idOnibus" -> andaDeOnibus = true
            }
        }

        val colunaAluno = getColumnIndex(csv, "eUmAluno")
        val colunaDengue = getColumnIndex(csv, "teveDengue")
        val colunaOnibus = getColumnIndex(csv, "andaDeOnibus")

        for(i in 1 until csv.size){
            eAluno = false
            teveDengue = false
            andaDeOnibus = false

            atualizarFlags(nomeChave)

            val linhas = mutableListOf( csv[i] )

            for((nomeChaveEstrangeira, tabela) in outrasTabelas){
                val colunaChave = getColumnIndex(csv, nomeChaveEstrangeira)
                val chaveEstrangeira = csv[i][colunaChave].toIntOrNull()
                if(chaveEstrangeira != null){
                    atualizarFlags(nomeChaveEstrangeira)

                    val (linhaTabela, pos) = getLinha(tabela, chaveEstrangeira.toString())

                    if(pos >= 0){
                        linhas.add(linhaTabela)
                        tabela.removeAt(pos)
                    }
                }
            }

            val linhaCompleta = if(linhas.size == 1){
                csv[i]
            }else{
                combinadorDeLinhas(linhas)
            }

            linhaCompleta[colunaAluno] = eAluno.toString()
            linhaCompleta[colunaDengue] = teveDengue.toString()
            linhaCompleta[colunaOnibus] = andaDeOnibus.toString()

            baseNova.add(linhaCompleta)
        }
    }

    juntarTabela("idAluno", csvAlunos, listOf( "idDengue" to csvDengue, "idOnibus" to csvOnibus ))
    juntarTabela("idDengue", csvDengue, listOf( "idAluno" to csvAlunos, "idOnibus" to csvOnibus ))
    juntarTabela("idOnibus", csvOnibus, listOf( "idAluno" to csvAlunos, "idDengue" to csvDengue ))

    baseNova.forEachIndexed { index, linha ->
        if(index > 0){
            linha[0] = (index - 1).toString()

            while(linha.size > ordemColunas.size){
                linha.removeAt(linha.size - 1)
            }
        }
    }

    writeCSV("${path}Base Completa.csv" , baseNova)
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

//fun compararDuplicatas(filepath: String, newFilepath: String){
//    val csv = readCSV(filepath)
//
//    val colunaNome = getColumnIndex(csv, "Nome")
//    val colunaNomePai = getColumnIndex(csv, "Nome do Pai")
//    val colunaNomeMae = getColumnIndex(csv, "Nome da Mae")
//    val colunaSexo = getColumnIndex(csv, "Sexo")
//    val colunaData = getColumnIndex(csv, "Data de Nascimento")
//    val colunaIdade = getColumnIndex(csv, "idade")
//    val colunaIdBairro = getColumnIndex(csv, "idBairro")
//
////    val jaroWrinklerOf = { linha1: MutableList<String> , linha2: MutableList<String>, coluna: Int  ->
////        jaroWinklerSimilarity(linha1[coluna], linha2[coluna])
////    }
//
//    val concatenarTudo = { linha: MutableList<String> ->
//        "${linha[colunaNome]}${linha[colunaNomePai]}${linha[colunaNomeMae]}${linha[colunaData]}${linha[colunaIdade]}${linha[colunaIdBairro]}${linha[colunaSexo]}"
//    }
//
////    val concatenarNomes = { linha: MutableList<String> ->
////        "${linha[colunaNome]}${linha[colunaNomePai]}${linha[colunaNomeMae]}"
////    }
//
//    println("Tudo concatenado:")
//    criarComparacaoPareamento(csv){ linha1, linha2 ->
//        val linha1Conc = concatenarTudo(linha1)
//        val linha2Conc = concatenarTudo(linha2)
//
//        jaroWinklerSimilarity(linha1Conc, linha2Conc)
//    }
//
//    writeCSV(newFilepath, csv)
//}

//data class ColunasDuplicadoComparacao(
//    val posColunaOriginal: Int,
//    val posColunaDuplicado: Int,
//    val nomeColunaPorc: String,
//    val algoritmo: TipoComparacao,
//    val pontoDeCorte: Double
//)
//
//fun pontuarDuplicados(nomeCSVInput: String, nomeCSVOutput: String){
//    val csv = readCSV("$path$nomeCSVInput")
//
//    val cd = listOf(
//        ColunasDuplicadoComparacao(getColumnIndex(csv, "Nome"), getColumnIndex(csv, "Nome Duplicado"), "nomePorc", JAROW, 0.8),
//        ColunasDuplicadoComparacao(getColumnIndex(csv, "Nome da Mae"), getColumnIndex(csv, "Nome da Mae Duplicado"), "nomeMaePorc", JAROW, 0.8),
//        ColunasDuplicadoComparacao(getColumnIndex(csv, "Nome do Pai"), getColumnIndex(csv, "Nome do Pai Duplicado"), "nomePaiPorc", JAROW, 0.8),
//        //ColunasDuplicadoComparacao("Sexo",  "Sexo Duplicado", "sexoPorc", EQUALS),
//        ColunasDuplicadoComparacao(getColumnIndex(csv, "Data de Nascimento"), getColumnIndex(csv, "Data de Nascimento Duplicado"), "nascPorc", LEVEN, 0.9),
//        ColunasDuplicadoComparacao(getColumnIndex(csv, "Bairro"), getColumnIndex(csv, "Bairro Duplicado"), "bairroPorc", LEVEN, 1.0)
//    )
//
//    pontuarDuplicados(cd, csv, nomeCSVOutput)
//}

//fun pontuarDuplicados(cd: List<ColunasDuplicadoComparacao>, csv: MutableList<MutableList<String>>, nomeCSVOutput: String){
//    val mapNovasCol = hashMapOf<String, Int>()
//
//    for(info in cd){
//        mapNovasCol[info.nomeColunaPorc] = addNewColumn(csv, info.nomeColunaPorc)
//    }
//
//    val colunaPontuacao = addNewColumn(csv, "pontuacao")
//
//    for(i in 1 until csv.size){
//        var pontuacao = 0
//        for(info in cd){
//            val posOriginal = info.posColunaOriginal
//            val posDuplicado = info.posColunaDuplicado
//
//            val original = csv[i][posOriginal]
//            val duplicado = csv[i][posDuplicado]
//            val porcentagem = if(original.isNotEmpty() && duplicado.isNotEmpty()) {
//                when (info.algoritmo) {
//                    JAROW -> {
//                        jaroWinklerSimilarity(original, duplicado)
//                    }
//                    LEVEN -> {
//                        levenshteinPercentage(original, duplicado)
//                    }
//                    EQUALS -> {
//                        if (original == (duplicado)) {
//                            1.0
//                        } else {
//                            0.0
//                        }
//                    }
//                }
//            }else{
//                0.0
//            }
//
//            if(porcentagem >= info.pontoDeCorte){
//                pontuacao++
//            }
//
//            mapNovasCol[info.nomeColunaPorc]?.let { pos ->
//                csv[i][pos] = porcentagem.toString()
//            }
//        }
//
//        csv[i][colunaPontuacao] = pontuacao.toString()
//    }
//
//    writeCSV("$path$nomeCSVOutput", csv)
//}