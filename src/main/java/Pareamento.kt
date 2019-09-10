import kotlin.system.measureTimeMillis

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

fun parearDuplicados(
    csv: MutableList<MutableList<String>>,
    nomeColunaIDCSV: String,
    nomeColunaPorcentagemCSV: String,
    comparador: (linha1: MutableList<String>, linha2: MutableList<String>) -> Double
){
    parearBases(csv, csv, nomeColunaIDCSV, nomeColunaIDCSV, nomeColunaPorcentagemCSV, nomeColunaPorcentagemCSV, true, comparador)
}

fun parearBases(
    csv1: MutableList<MutableList<String>>,
    csv2: MutableList<MutableList<String>>,
    nomeColunaIDCSV1: String, //Nome da coluna do id do csv2 no csv1
    nomeColunaIDCSV2: String, //Nome da coluna do id do csv1 no csv2
    nomeColunaPorcentagemCSV1: String,
    nomeColunaPorcentagemCSV2: String,
    mesmaBase: Boolean = false,
    comparador: (linha1: MutableList<String>, linha2: MutableList<String>) -> Double
){
    val tempo = measureTimeMillis {
        val colunaIDCSV1 = addNewColumn(csv1, nomeColunaIDCSV1)
        val colunaPorcentagemCSV1 = addNewColumn(csv1, nomeColunaPorcentagemCSV1)

        var colunaIDCSV2 = -1
        var colunaPorcentagemCSV2 = -1
        if(!mesmaBase) {
            colunaIDCSV2 = addNewColumn(csv2, nomeColunaIDCSV2)
            colunaPorcentagemCSV2 = addNewColumn(csv2, nomeColunaPorcentagemCSV2)
        }

        for(i in 1 until csv1.size){
            var maiorPorcentagem = Double.MIN_VALUE
            var posMaior = -1

            val linhaCSV1 = csv1[i]
            for(j in 1 until csv2.size){
                //Checa se é a mesma linha, usar essa flag apenas quando comparar uma base com ela mesma
                if(!mesmaBase || i != j){
                    val porcentagem = comparador(linhaCSV1, csv2[j])
                    if(porcentagem > maiorPorcentagem){
                        maiorPorcentagem = porcentagem
                        posMaior = j
                    }
                }
            }

            if(posMaior > -1){
                //Assume que a coluna do id é a primeira nas duas tabelas
                csv1[i][colunaIDCSV1] = csv2[posMaior][0]
                csv1[i][colunaPorcentagemCSV1] = maiorPorcentagem.toString()

                if(!mesmaBase){
                    csv2[posMaior][colunaIDCSV2] = csv1[i][0]
                    csv2[posMaior][colunaPorcentagemCSV2] = maiorPorcentagem.toString()
                }
            }
        }
    }

    println("Tempo pareamento: ${tempo/1000} segundos")
}

fun parearBairros(bairrosListPath: String, sourcePath: String, endPath: String){
    writeCSV(endPath, parearBairros(readCSV(sourcePath), bairrosListPath))
}