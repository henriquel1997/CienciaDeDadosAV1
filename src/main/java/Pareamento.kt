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

fun parearBases(
    csv1: MutableList<MutableList<String>>,
    csv2: MutableList<MutableList<String>>,
    nomeColunaIDCSV1: String, //Nome da coluna do id do csv2 no csv1
    nomeColunaIDCSV2: String, //Nome da coluna do id do csv1 no csv2
    porcentagemIgual: Double,
    comparador: (linha1: MutableList<String>, linha2: MutableList<String>) -> Double
){
    val tempo = measureTimeMillis {
        val colunaIDCSV1 = addNewColumn(csv1, nomeColunaIDCSV1)
        val colunaIDCSV2 = addNewColumn(csv2, nomeColunaIDCSV2)

        for(i in 1 until csv1.size){
            var maiorPorcentagem = Double.MIN_VALUE
            var posMaior = -1

            val linhaCSV1 = csv1[i]
            for(j in 1 until csv2.size){
                //Checa se é a mesma linha, usar essa flag apenas quando comparar uma base com ela mesma
                val porcentagem = comparador(linhaCSV1, csv2[j])
                if(porcentagem > maiorPorcentagem){
                    maiorPorcentagem = porcentagem
                    posMaior = j
                }
            }

            if(maiorPorcentagem >= porcentagemIgual){
                //Assume que a coluna do id é a primeira nas duas tabelas
                csv1[i][colunaIDCSV1] = csv2[posMaior][0]
                csv2[posMaior][colunaIDCSV2] = csv1[i][0]
            }
        }
    }

    println("Tempo pareamento: ${tempo/1000} segundos")
}

fun removerDuplicados(
    csv: MutableList<MutableList<String>>,
    nomeColunaID: String,
    porcentagemIgual: Double,
    comparador: (linha1: MutableList<String>, linha2: MutableList<String>) -> Double,
    comparadorMaisCompleto: (linha1: MutableList<String>, linha2: MutableList<String>) -> Boolean
){
    val tempo = measureTimeMillis {
        val colunaID = addNewColumn(csv, nomeColunaID)

        for(i in 1 until csv.size){
            val listaIguais = mutableListOf<Int>()

            val linhaCSV = csv[i]
            for(j in 1 until csv.size){
                //Checa se não é a mesma linha
                if(i != j){
                    val porcentagem = comparador(linhaCSV, csv[j])
                    if(porcentagem > porcentagemIgual){
                        listaIguais.add(j)
                    }
                }
            }

            //TODO: Remoção não está testada
            var maisCompleto = linhaCSV
            var posMaisCompleto = i
            for(pos in listaIguais){
                if(comparadorMaisCompleto(csv[pos], maisCompleto)){
                    maisCompleto = csv[pos]
                    posMaisCompleto = pos
                }
            }

            var cont = 0
            for(pos in listaIguais){
                if(pos != posMaisCompleto){
                    csv.removeAt(pos - cont)
                    cont++
                }
            }

            if(posMaisCompleto != i){
                csv.removeAt(i - cont)
            }
        }
    }

    println("Tempo pareamento: ${tempo/1000} segundos")
}

fun criarComparacaoPareamento(
    csv: MutableList<MutableList<String>>,
    comparador: (linha1: MutableList<String>, linha2: MutableList<String>) -> Double
){
    val tempo = measureTimeMillis {
        val colunaNome = getColumnIndex(csv, "Nome")
        val colunaNomePai = getColumnIndex(csv, "Nome do Pai")
        val colunaNomeMae = getColumnIndex(csv, "Nome da Mae")
        val colunaNomeDup = addNewColumn(csv, "Nome Duplicado")
        val colunaNomeMaeDup = addNewColumn(csv, "Nome da Mae Duplicado")
        val colunaNomePaiDup = addNewColumn(csv, "Nome do Pai Duplicado")
        val colunaId = addNewColumn(csv, "id Duplicado")
        val colunaPorcentagemCSV = addNewColumn(csv, "porcentagem")

        for(i in 1 until csv.size){
            var maiorPorcentagem = Double.MIN_VALUE
            var posMaior = -1

            val linha = csv[i]
            for(j in 1 until csv.size){
                if(i != j){
                    val porcentagem = comparador(linha, csv[j])
                    if(porcentagem > maiorPorcentagem){
                        maiorPorcentagem = porcentagem
                        posMaior = j
                    }
                }
            }

            if(posMaior > -1){
                csv[i][colunaId] = posMaior.toString()
                csv[i][colunaPorcentagemCSV] = maiorPorcentagem.toString()
                csv[i][colunaNomeDup] = csv[posMaior][colunaNome]
                csv[i][colunaNomePaiDup] = csv[posMaior][colunaNomePai]
                csv[i][colunaNomeMaeDup] = csv[posMaior][colunaNomeMae]
            }
        }

        csv.sortByDescending {
            it[colunaPorcentagemCSV]
        }
    }

    println("Tempo pareamento: ${tempo/1000} segundos")
}

fun parearBairros(bairrosListPath: String, sourcePath: String, endPath: String){
    writeCSV(endPath, parearBairros(readCSV(sourcePath), bairrosListPath))
}