import kotlin.system.measureTimeMillis

fun parearBairros(csv: MutableList<MutableList<String>>, bairrosListPath: String): MutableList<MutableList<String>> {
    val tempo = measureTimeMillis {
        val colunaBairro = getColumnIndex(csv, "Bairro")
        if(colunaBairro > -1){
            val bairros = readTXTList(bairrosListPath)
            //val colunaPorcSimilar = addNewColumn(csv, "porcBairro")
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

                //csv[i][colunaPorcSimilar] = maiorPorc.toString()
                csv[i][colunaBairro] = bairroMaisParecido
                csv[i][colunaIDBairroPareado] = idBairroMaisParecido.toString()
            }

            println("Menor porcentagem: $menorPorcentagem - Nome: $nomeBairro, Mais parecido: $nomeMaisParecido")
        }
    }

    println("Tempo pareamento bairros: ${tempo/1000} segundos")

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
    var numPareamentos = 0
    val tempo = measureTimeMillis {
        val colunaIDCSV1 = addNewColumn(csv1, nomeColunaIDCSV1)
        val colunaIDCSV2 = addNewColumn(csv2, nomeColunaIDCSV2)

        for(i in 1 until csv1.size){
            var maiorPorcentagem = Double.MIN_VALUE
            var posMaior = -1

            val linhaCSV1 = csv1[i]
            for(j in 1 until csv2.size){
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
                numPareamentos++
            }
        }
    }
    println("Número de pareamentos: $numPareamentos")
    println("Tempo pareamento: ${tempo/1000} segundos")
}

fun removerDuplicados(
    csv: MutableList<MutableList<String>>,
    pontoDeCorte: Double,
    comparador: (linha1: MutableList<String>, linha2: MutableList<String>) -> Double,
    combinadorDeDuplicatas: (List<List<String>>) -> MutableList<String>
){
    val tamanhoInicial = csv.size

    val tempo = measureTimeMillis {
        var i = 1
        while(i < csv.size){
            val listaIguais = mutableListOf<Int>()

            val linhaCSV = csv[i]
            for(j in 1 until csv.size){
                //Checa se não é a mesma linha
                if(i != j){
                    if(comparador(linhaCSV, csv[j]) >= pontoDeCorte){
                        listaIguais.add(j)
                    }
                }
            }

            if(listaIguais.isNotEmpty()){
                listaIguais.add(0, i)

                val duplicatas = mutableListOf<MutableList<String>>()

                listaIguais.forEach{ pos ->
                    duplicatas.add(csv[pos])
                }

                csv[i] = combinadorDeDuplicatas(duplicatas)

                listaIguais.removeAt(0)
                listaIguais.sort()

                for((cont, pos) in listaIguais.withIndex()){
                    csv.removeAt(pos - cont)
                }
            }

            i++
        }

        for((index, linha) in csv.withIndex()){
            if(index > 0){
                linha[0] = (index - 1).toString()
            }
        }
    }

    val tamanhoFinal = csv.size
    println("Tempo pareamento: ${tempo/1000} segundos")
    println("Tamanho inicial: $tamanhoInicial")
    println("Tamanho final: $tamanhoFinal")
    println("Linhas removidas: ${tamanhoInicial - tamanhoFinal}")
}

fun criarComparacaoPareamento(
    csv: MutableList<MutableList<String>>,
    comparador: (linha1: MutableList<String>, linha2: MutableList<String>) -> Double
){
    val tempo = measureTimeMillis {
        val colunaNome = getColumnIndex(csv, "Nome")
        val colunaNomePai = getColumnIndex(csv, "Nome do Pai")
        val colunaNomeMae = getColumnIndex(csv, "Nome da Mae")
        val colunaSexo = getColumnIndex(csv, "Sexo")
        val colunaDataNascimento = getColumnIndex(csv, "Data de Nascimento")
        val colunaBairro = getColumnIndex(csv, "Bairro")
        val colunaIdade = getColumnIndex(csv, "idade")

        val colunaNomeDup = addNewColumn(csv, "Nome Duplicado")
        val colunaNomeMaeDup = addNewColumn(csv, "Nome da Mae Duplicado")
        val colunaNomePaiDup = addNewColumn(csv, "Nome do Pai Duplicado")
        val colunaSexoDup = addNewColumn(csv, "Sexo Duplicado")
        val colunaDataNascimentoDup = addNewColumn(csv, "Data de Nascimento Duplicado")
        val colunaBairroDup = addNewColumn(csv, "Bairro Duplicado")
        val colunaIdadeDup = addNewColumn(csv, "Idade Duplicado")

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
                csv[i][colunaId] = (posMaior - 1).toString()
                csv[i][colunaPorcentagemCSV] = maiorPorcentagem.toString()
                csv[i][colunaNomeDup] = csv[posMaior][colunaNome]
                csv[i][colunaNomePaiDup] = csv[posMaior][colunaNomePai]
                csv[i][colunaNomeMaeDup] = csv[posMaior][colunaNomeMae]
                csv[i][colunaSexoDup] = csv[posMaior][colunaSexo]
                csv[i][colunaDataNascimentoDup] = csv[posMaior][colunaDataNascimento]
                csv[i][colunaBairroDup] = csv[posMaior][colunaBairro]
                csv[i][colunaIdadeDup] = csv[posMaior][colunaIdade]
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