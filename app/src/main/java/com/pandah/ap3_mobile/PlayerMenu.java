package com.pandah.ap3_mobile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerMenu extends AppCompatActivity
{
    private TextView tvPlayerName;
    private TextView tvPlayerPoints;
    private TextView tvPlayerRanking;

    private int playerPoints;
    private int playerRanking;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private final Random random = new Random();

    private final List<Question> questionsLevel1 = new ArrayList<>();
    private final List<Question> questionsLevel2 = new ArrayList<>();
    private final List<Question> questionsLevel3 = new ArrayList<>();
    private final List<Question> questionsLevel4 = new ArrayList<>();
    private final List<Question> questionsLevel5 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_menu);

        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvPlayerPoints = findViewById(R.id.tvPlayerPoints);
        tvPlayerRanking = findViewById(R.id.tvPlayerRanking);

        Button btnLevel1 = findViewById(R.id.btnLevel1);
        Button btnLevel2 = findViewById(R.id.btnLevel2);
        Button btnLevel3 = findViewById(R.id.btnLevel3);
        Button btnLevel4 = findViewById(R.id.btnLevel4);
        Button btnLevel5 = findViewById(R.id.btnLevel5);
        Button btnReset = findViewById(R.id.btnReset);

        prefs = getSharedPreferences("player_data", MODE_PRIVATE);
        editor = prefs.edit();

        String playerName = prefs.getString("player_name", "Unknown Player");
        playerPoints = prefs.getInt("player_points", 0);
        playerRanking = prefs.getInt("player_ranking", 0);

        if (playerRanking == 0)
        {
            playerRanking = 10;
            editor.putInt("player_ranking", playerRanking);
            editor.apply();
        }

        tvPlayerName.setText(playerName);
        tvPlayerPoints.setText("Points: " + playerPoints);
        tvPlayerRanking.setText("Ranking: " + playerRanking);

        loadQuestions();

        btnLevel1.setOnClickListener(v -> showRandomQuestion(questionsLevel1, 10));
        btnLevel2.setOnClickListener(v -> showRandomQuestion(questionsLevel2, 20));
        btnLevel3.setOnClickListener(v -> showRandomQuestion(questionsLevel3, 30));
        btnLevel4.setOnClickListener(v -> showRandomQuestion(questionsLevel4, 50));
        btnLevel5.setOnClickListener(v -> showRandomQuestion(questionsLevel5, 100));

        btnReset.setOnClickListener(v ->
        {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmação")
                    .setMessage("Tem certeza que deseja resetar todos os dados?")
                    .setPositiveButton("Sim", (dialog, which) -> resetData())
                    .setNegativeButton("Não", null)
                    .show();
        });
    }

    private void showRandomQuestion(List<Question> questions, int pointsToAdd)
    {
        if (questions.isEmpty()) return;

        int index = random.nextInt(questions.size());
        Question q = questions.get(index);

        showQuestionDialog(q.question, q.options, q.correctAnswerIndex, pointsToAdd);
    }

    private void showQuestionDialog(String question, String[] options, int correctAnswerIndex, int pointsToAdd)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(question);

        builder.setSingleChoiceItems(options, -1, null);

        builder.setPositiveButton("Responder", (dialog, whichButton) ->
        {
            AlertDialog alert = (AlertDialog) dialog;
            int selectedPosition = alert.getListView().getCheckedItemPosition();

            if (selectedPosition == correctAnswerIndex)
            {
                addPoints(pointsToAdd);
                showMessage("Correto! Você ganhou " + pointsToAdd + " pontos.");
            }
            else if (selectedPosition == -1)
            {
                showMessage("Você precisa selecionar uma alternativa!");
            }
            else
            {
                showMessage("Errado! Tente novamente.");
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void addPoints(int points)
    {
        playerPoints += points;

        int rankDecrease = playerPoints / 100;
        int newRanking = 10 - rankDecrease;

        if (newRanking < 1) {
            newRanking = 1;
        }

        if (newRanking != playerRanking) {
            playerRanking = newRanking;
            editor.putInt("player_ranking", playerRanking);
        }

        editor.putInt("player_points", playerPoints);
        editor.apply();

        tvPlayerPoints.setText("Points: " + playerPoints);
        tvPlayerRanking.setText("Ranking: " + playerRanking);
    }

    private void resetData()
    {
        String currentName = prefs.getString("player_name", "Unknown Player");

        editor.clear();
        editor.apply();

        editor.putString("player_name", currentName);
        editor.putInt("player_ranking", 10);
        editor.putInt("player_points", 0);
        editor.apply();

        playerPoints = 0;
        playerRanking = 10;

        tvPlayerPoints.setText("Points: " + playerPoints);
        tvPlayerRanking.setText("Ranking: " + playerRanking);

        showMessage("Dados resetados com sucesso!");
    }

    private void showMessage(String message)
    {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    static class Question
    {
        String question;
        String[] options;
        int correctAnswerIndex;

        Question(String question, String[] options, int correctAnswerIndex)
        {
            this.question = question;
            this.options = options;
            this.correctAnswerIndex = correctAnswerIndex;
        }
    }

    private void loadQuestions()
    {
        questionsLevel1.add(new Question("O que é um sistema operacional?",
                new String[]{"Uma rede social", "Um software que gerencia hardware e software", "Um antivírus", "Um jogo"}, 1));

        questionsLevel1.add(new Question("O que significa 'bug' em programação?",
                new String[]{"Melhoria", "Erro no código", "Licença", "Design"}, 1));

        questionsLevel1.add(new Question("Qual linguagem é mais utilizada para desenvolvimento web?",
                new String[]{"C++", "JavaScript", "Python", "Kotlin"}, 1));

        questionsLevel1.add(new Question("Qual é a função da memória RAM?",
                new String[]{"Armazenamento permanente", "Armazenar dados temporários", "Fornecer energia", "Proteger contra vírus"}, 1));

        questionsLevel1.add(new Question("O que é um aplicativo mobile?",
                new String[]{"Um site", "Um software para celular", "Um hardware", "Um servidor"}, 1));

        questionsLevel1.add(new Question("O que significa 'www' na internet?",
                new String[]{"Wide Windows Web", "World Wide Web", "Windows Wireless Web", "Wireless World Web"}, 1));

        questionsLevel1.add(new Question("O que é um navegador de internet?",
                new String[]{"Word", "Google Chrome", "Excel", "Paint"}, 1));

        questionsLevel1.add(new Question("O que é um arquivo .exe?",
                new String[]{"Imagem", "Arquivo executável", "Texto", "Vídeo"}, 1));

        questionsLevel1.add(new Question("O que é HTML?",
                new String[]{"Linguagem de programação", "Linguagem de marcação para páginas web", "Banco de dados", "Sistema operacional"}, 1));

        questionsLevel1.add(new Question("O que é um link na internet?",
                new String[]{"Arquivo de áudio", "Endereço que leva a uma página web", "Programa antivírus", "Código de erro"}, 1));

        questionsLevel1.add(new Question("O que faz o botão 'Salvar' em um programa?",
                new String[]{"Abre arquivo", "Fecha o programa", "Armazena o arquivo atual", "Imprime o documento"}, 1));

        questionsLevel1.add(new Question("Qual dispositivo é usado para digitar?",
                new String[]{"Mouse", "Teclado", "Monitor", "Impressora"}, 1));

        questionsLevel1.add(new Question("O que é Wi-Fi?",
                new String[]{"Um tipo de vírus", "Rede sem fio para internet", "Um software", "Um hardware"}, 1));

        questionsLevel1.add(new Question("O que é um arquivo PDF?",
                new String[]{"Documento portátil", "Imagem", "Vídeo", "Áudio"}, 1));

        questionsLevel1.add(new Question("O que significa USB?",
                new String[]{"Universal Serial Bus", "Unilateral Software Backup", "Universal System Box", "Unsorted Serial Bus"}, 1));


        questionsLevel2.add(new Question("O que é front-end?",
                new String[]{"Banco de dados", "Parte visual de um site/app", "Servidor", "Backend"}, 1));

        questionsLevel2.add(new Question("Em qual linguagem o Android é tradicionalmente desenvolvido?",
                new String[]{"Swift", "Java", "Python", "PHP"}, 1));

        questionsLevel2.add(new Question("O que é uma API?",
                new String[]{"Antivírus", "Interface gráfica", "Regras para comunicação entre sistemas", "Banco de dados"}, 2));

        questionsLevel2.add(new Question("O que significa responsividade?",
                new String[]{"Velocidade", "Servidor ativo", "Adaptar tela a diferentes dispositivos", "Erro de navegador"}, 2));

        questionsLevel2.add(new Question("O que faz um banco de dados?",
                new String[]{"Processa vídeo", "Armazena informações", "Gera gráficos", "Conecta hardware"}, 1));

        questionsLevel2.add(new Question("O que é backend?",
                new String[]{"Design", "Lógica e banco de dados", "Botões", "Layout"}, 1));

        questionsLevel2.add(new Question("O que é um framework?",
                new String[]{"Um antivírus", "Uma estrutura pronta para desenvolvimento", "Um navegador", "Um hardware"}, 1));

        questionsLevel2.add(new Question("O que é hospedagem de sites?",
                new String[]{"Armazenamento local", "Serviço para disponibilizar sites na internet", "Um navegador", "Banco de dados"}, 1));

        questionsLevel2.add(new Question("O que é CSS?",
                new String[]{"Linguagem de estilo para páginas web", "Banco de dados", "Software antivírus", "Linguagem de programação"}, 1));

        questionsLevel2.add(new Question("O que é UX?",
                new String[]{"User Experience - Experiência do usuário", "Unidade de Xadrez", "Uma API", "Um software"}, 1));

        questionsLevel2.add(new Question("O que é JavaScript?",
                new String[]{"Linguagem para programação web", "Sistema operacional", "Banco de dados", "Hardware"}, 1));

        questionsLevel2.add(new Question("O que é FTP?",
                new String[]{"Protocolo para transferência de arquivos", "Banco de dados", "Um navegador", "Um antivírus"}, 1));

        questionsLevel2.add(new Question("O que é um CMS?",
                new String[]{"Sistema de gerenciamento de conteúdo", "Um tipo de malware", "Framework mobile", "Banco de dados"}, 1));

        questionsLevel2.add(new Question("O que é HTTPS?",
                new String[]{"Protocolo seguro de comunicação na web", "Uma linguagem de programação", "Um software de edição", "Um hardware"}, 1));

        questionsLevel2.add(new Question("O que é cache em informática?",
                new String[]{"Memória temporária para acelerar processos", "Um vírus", "Um hardware externo", "Um navegador"}, 1));


        questionsLevel3.add(new Question("O que é back-end?",
                new String[]{"Front-end", "Parte que lida com dados e regras de negócio", "Design gráfico", "API"}, 1));

        questionsLevel3.add(new Question("O que significa IDE?",
                new String[]{"Interface de Dados Externa", "Ambiente de Desenvolvimento Integrado", "Interação Digital", "Servidor"}, 1));

        questionsLevel3.add(new Question("O que é uma requisição HTTP GET?",
                new String[]{"Envia dados", "Solicita dados do servidor", "Exclui dados", "Atualiza DNS"}, 1));

        questionsLevel3.add(new Question("O que é o Firebase?",
                new String[]{"IDE", "Plataforma backend da Google", "Servidor físico", "Banco de dados offline"}, 1));

        questionsLevel3.add(new Question("O que significa open-source?",
                new String[]{"Software pago", "Código aberto e livre", "Privado", "Antivírus"}, 1));

        questionsLevel3.add(new Question("O que é um servidor?",
                new String[]{"Aplicativo", "Computador que fornece serviços para outros", "Celular", "Banco offline"}, 1));

        questionsLevel3.add(new Question("O que é JSON?",
                new String[]{"Imagem", "Formato de dados leve", "Vídeo", "Plugin de navegador"}, 1));

        questionsLevel3.add(new Question("O que é SQL?",
                new String[]{"Linguagem para criação de layout", "Linguagem para banco de dados", "Antivírus", "IDE"}, 1));

        questionsLevel3.add(new Question("O que é REST?",
                new String[]{"Um tipo de servidor", "Arquitetura para APIs web", "Banco de dados", "Linguagem de programação"}, 1));

        questionsLevel3.add(new Question("O que é OAuth?",
                new String[]{"Protocolo de autenticação", "Software antivírus", "Editor de texto", "Banco de dados"}, 1));

        questionsLevel3.add(new Question("O que é middleware?",
                new String[]{"Software intermediário entre sistemas", "Banco de dados", "Hardware", "Editor de vídeo"}, 1));

        questionsLevel3.add(new Question("O que é um container?",
                new String[]{"Hardware para armazenamento", "Ambiente isolado para apps", "Framework", "IDE"}, 1));

        questionsLevel3.add(new Question("O que é um token JWT?",
                new String[]{"Arquivo de vídeo", "Token para autenticação segura", "Banco de dados", "Editor de texto"}, 1));

        questionsLevel3.add(new Question("O que é escalonamento de processos?",
                new String[]{"Gerenciamento de execução de tarefas", "Criação de gráficos", "Banco de dados", "Design de interface"}, 1));

        questionsLevel3.add(new Question("O que é uma API RESTful?",
                new String[]{"API que segue princípios REST", "Software de edição", "Banco de dados", "Hardware"}, 1));


        questionsLevel4.add(new Question("O que é escalabilidade?",
                new String[]{"Aumentar botões", "Capacidade de crescer sem perder desempenho", "Design bonito", "Reduzir bugs"}, 1));

        questionsLevel4.add(new Question("No desenvolvimento mobile, o que é um layout constraint?",
                new String[]{"Banco de dados", "Posicionamento de elementos via restrições", "Servidor", "Algoritmo de busca"}, 1));

        questionsLevel4.add(new Question("O que faz o protocolo HTTPS?",
                new String[]{"Aumenta velocidade", "Garante comunicação segura", "Armazena arquivos", "Roda comandos"}, 1));

        questionsLevel4.add(new Question("O que é um design pattern?",
                new String[]{"Tema gráfico", "Solução reutilizável de desenvolvimento", "API", "Banco relacional"}, 1));

        questionsLevel4.add(new Question("O que é CI/CD?",
                new String[]{"Linguagem", "Automação de integração e deploy", "Framework", "Protocolo"}, 1));

        questionsLevel4.add(new Question("O que é um sistema distribuído?",
                new String[]{"Servidor único", "Vários computadores trabalhando juntos", "Design gráfico", "Banco de imagens"}, 1));

        questionsLevel4.add(new Question("O que é escalonamento em sistemas operacionais?",
                new String[]{"Criar animações", "Organizar a execução de processos", "Aumentar memória RAM", "Melhorar design"}, 1));

        questionsLevel4.add(new Question("O que é tolerância a falhas?",
                new String[]{"Capacidade de continuar funcionando apesar de erros", "Um tipo de vírus", "Framework", "Banco de dados"}, 1));

        questionsLevel4.add(new Question("O que é um microkernel?",
                new String[]{"Núcleo pequeno que gerencia funções básicas do sistema", "Um tipo de hardware", "Banco de dados", "Software antivírus"}, 1));

        questionsLevel4.add(new Question("O que é programação reativa?",
                new String[]{"Programação que reage a eventos e mudanças", "Desenvolvimento de jogos", "Banco de dados", "Design gráfico"}, 1));

        questionsLevel4.add(new Question("O que é container orchestration?",
                new String[]{"Gerenciamento automático de containers", "Programação mobile", "Banco de dados", "Interface gráfica"}, 1));

        questionsLevel4.add(new Question("O que é eventual consistency?",
                new String[]{"Consistência eventual em sistemas distribuídos", "Tipo de framework", "Linguagem de programação", "Editor de texto"}, 1));

        questionsLevel4.add(new Question("O que é o padrão Singleton?",
                new String[]{"Garantir uma única instância de uma classe", "Design de banco de dados", "Protocolo de rede", "Linguagem de programação"}, 1));

        questionsLevel4.add(new Question("O que é análise estática de código?",
                new String[]{"Verificação de código sem executar o programa", "Teste de hardware", "Banco de dados", "Interface de usuário"}, 1));

        questionsLevel4.add(new Question("O que é a técnica de 'debounce' em programação?",
                new String[]{"Evitar execução repetida de função em curto intervalo", "Criar gráficos", "Banco de dados", "Gerar relatórios"}, 1));


        questionsLevel5.add(new Question("O que significa arquitetura de microsserviços?",
                new String[]{"Servidor físico", "Sistema dividido em pequenos serviços independentes", "Modelo de hardware", "Tema gráfico"}, 1));

        questionsLevel5.add(new Question("O que é SOLID?",
                new String[]{"Framework", "Princípios para código limpo", "Compilador", "Banco"}, 1));

        questionsLevel5.add(new Question("O que faz um container Docker?",
                new String[]{"Antivírus", "Isola apps em ambientes portáteis", "Framework mobile", "Protocolo de rede"}, 1));

        questionsLevel5.add(new Question("O que é Machine Learning?",
                new String[]{"Editor de texto", "Hardware de rede", "Sistemas que aprendem com dados", "Banco de dados"}, 2));

        questionsLevel5.add(new Question("O que significa Clean Architecture?",
                new String[]{"Compilador", "Separação de regras de negócio, interface e dados", "Protocolo", "Deploy"}, 1));

        questionsLevel5.add(new Question("O que é Kubernetes?",
                new String[]{"IDE", "Orquestrador de containers", "Antivírus", "Framework frontend"}, 1));

        questionsLevel5.add(new Question("O que é Inteligência Artificial?",
                new String[]{"Um navegador", "Simular inteligência humana em máquinas", "Uma API de vídeos", "Banco relacional"}, 1));

        questionsLevel5.add(new Question("O que significa Serverless?",
                new String[]{"Sem internet", "Executar código sem gerenciar servidores diretamente", "Servidor offline", "Banco sem dados"}, 1));

        questionsLevel5.add(new Question("O que é DevOps?",
                new String[]{"Desenvolvimento e operações integrados", "Software de design", "Banco de dados", "Hardware"}, 1));

        questionsLevel5.add(new Question("O que é uma arquitetura orientada a eventos?",
                new String[]{"Sistema baseado em eventos para comunicação", "Framework mobile", "Banco de dados", "Interface gráfica"}, 1));

        questionsLevel5.add(new Question("O que é infraestrutura como código (IaC)?",
                new String[]{"Gerenciar infraestrutura com arquivos de configuração", "Software de segurança", "Banco de dados", "IDE"}, 1));

        questionsLevel5.add(new Question("O que é containerização?",
                new String[]{"Empacotar apps em containers para portabilidade", "Criar design responsivo", "Banco de dados", "Testes automatizados"}, 1));

        questionsLevel5.add(new Question("O que é uma pipeline de CI/CD?",
                new String[]{"Automação de build, testes e deploy", "Banco de dados", "Framework mobile", "Servidor físico"}, 1));

        questionsLevel5.add(new Question("O que é uma função lambda?",
                new String[]{"Função anônima usada em programação funcional", "Software antivírus", "Banco de dados", "Editor de texto"}, 1));

        questionsLevel5.add(new Question("O que é observabilidade em sistemas?",
                new String[]{"Capacidade de monitorar e entender o sistema", "Software de edição", "Banco de dados", "Interface de usuário"}, 1));
    }
}
