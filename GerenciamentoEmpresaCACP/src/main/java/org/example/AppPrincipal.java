package org.example;


import com.sun.net.httpserver.Request;
import org.example.api.Empresas.Empresa;
import org.example.api.Empresas.EmpresaDTO;
import org.example.api.Services;
import org.example.api.Usuarios.UsuarioDTO;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Objects;

import static org.example.Cores.*;
import static org.example.Fontes.FONT_LABEL;
import static org.example.Fontes.FONT_TITLE;
import static org.example.Tabelas.modeloClientes;
import static org.example.Tabelas.tabelaUsuarios;

public class AppPrincipal {
    static void main() {
        LoginFrame login = new LoginFrame();
        login.setVisible(true);
    }
}

class LoginFrame extends JFrame {

    private final JTextField txtUsuario;
    private final JPasswordField txtSenha;
    private final JButton btnEntrar;
    private int tentativas = 0;
    private Timer timerBloqueio;
    private int segundosRestantes = 0;

    public static String token;


    public LoginFrame() {
        setTitle("CACP EMPRESAS");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(650, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel painel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(30, 30, 30),
                        getWidth(), getHeight(), new Color(60, 60, 60));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        painel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblTitulo = new JLabel("FAÇA SEU LOGIN");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitulo.setForeground(BLUE_LIGHT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        painel.add(lblTitulo, gbc);

        gbc.gridwidth = 1;

        JLabel lblUsuario = new JLabel("USUÁRIO:");
        lblUsuario.setFont(FONT_LABEL);
        lblUsuario.setForeground(FOREGROUND);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        painel.add(lblUsuario, gbc);

        txtUsuario = new JTextField(20);
        estilizarCampo(txtUsuario, new Color(70, 70, 70));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        painel.add(txtUsuario, gbc);

        JLabel lblSenha = new JLabel("SENHA:");
        lblSenha.setFont(FONT_LABEL);
        lblSenha.setForeground(FOREGROUND);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        painel.add(lblSenha, gbc);

        txtSenha = new JPasswordField(20);
        estilizarCampo(txtSenha, new Color(70, 70, 70));
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        painel.add(txtSenha, gbc);

        btnEntrar = new BotaoCustom("ENTRAR");
        btnEntrar.setFocusPainted(false);
        btnEntrar.setContentAreaFilled(false);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setFont(FONT_LABEL);
        btnEntrar.setForeground(FOREGROUND);
        btnEntrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 15, 15, 15);
        painel.add(btnEntrar, gbc);

        JLabel lblFooter = new JLabel("© 1997 CACP Contabilidade Ltda - Todos os direitos reservados");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblFooter.setForeground(new Color(150, 150, 150));
        gbc.gridy = 4;
        gbc.insets = new Insets(30, 15, 0, 15);
        painel.add(lblFooter, gbc);

        //Listeners
        txtUsuario.addActionListener(e -> txtSenha.requestFocusInWindow());

        txtSenha.addActionListener(e -> fazerLogin());

        btnEntrar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                corAnimada(BLUE_LIGHT, BLUE_DARK);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                corAnimada(BLUE_DARK, BLUE_LIGHT);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                fazerLogin();
            }
        });

        setContentPane(painel);
        getRootPane().setDefaultButton(btnEntrar);
    }

    private static class BotaoCustom extends JButton {

        private Color corAtual;

        public BotaoCustom(String text) {
            super(text);
            this.corAtual = BLUE_LIGHT;
        }

        public void setCurrentColor(Color color) {
            this.corAtual = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(corAtual);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

            FontMetrics fm = g2d.getFontMetrics();
            Rectangle2D textBounds = fm.getStringBounds(getText(), g2d);
            int x = (getWidth() - (int) textBounds.getWidth()) / 2;
            int y = (getHeight() - (int) textBounds.getHeight()) / 2 + fm.getAscent();
            g2d.setColor(getForeground());
            g2d.drawString(getText(), x, y);

            g2d.dispose();
        }
    }

    private void corAnimada(Color from, Color to) {
        final int duration = 200;
        final long startTime = System.currentTimeMillis();
        final Timer colorTimer = new Timer(10, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1f, (float) elapsed / duration);

            int r = (int) (from.getRed() + progress * (to.getRed() - from.getRed()));
            int g = (int) (from.getGreen() + progress * (to.getGreen() - from.getGreen()));
            int b = (int) (from.getBlue() + progress * (to.getBlue() - from.getBlue()));

            ((BotaoCustom) btnEntrar).setCurrentColor(new Color(r, g, b));

            if (progress >= 1f) {
                ((Timer) e.getSource()).stop();
            }
        });
        colorTimer.start();
    }

    private boolean loginApi() {
        String usuario = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());

        String APIurl = "https://36c6480ea827.ngrok-free.app/public/usuarios/login";
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        String json = String.format("{\"login\":\"%s\",\"senha\":\"%s\"}", usuario, senha);

        RequestBody corpoJson = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(APIurl)
                .post(corpoJson)
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            if (response.body().string().equals("Falha na autenticação Bad credentials")) {
                adicionarTentativa();
                String errorMsg = "Nome de Usuário ou senha inválidos";

                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, errorMsg, "Erro ao entrar", JOptionPane.ERROR_MESSAGE));
            } else {
                Response texto = client.newCall(request).execute();
                assert texto.body() != null;
                token = texto.body().string();
                return true;
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                    "Erro de Conexão: " + e.getMessage(),
                    "Erro ao conectar", JOptionPane.ERROR_MESSAGE));
        }
        return false;
    }

    private void estilizarCampo(JTextField campo, Color bg) {
        campo.setBackground(bg);
        campo.setForeground(FOREGROUND);
        campo.setCaretColor(FOREGROUND);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BLUE_LIGHT, 2, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        campo.setFont(new Font("SansSerif", Font.PLAIN, 16));
    }

    private void adicionarTentativa() {
        tentativas++;
        if (tentativas >= 3) {
            iniciarBloqueio();
        }
    }

    private void fazerLogin() {
        if (timerBloqueio != null && timerBloqueio.isRunning()) {
            JOptionPane.showMessageDialog(rootPane,
                    "Ainda faltam " + formatarTempo(segundosRestantes)
                            + " para tentar novamente", "Aguarde", JOptionPane.INFORMATION_MESSAGE);
        } else if (loginApi()) {
            AppFrame app = new AppFrame();
            app.setVisible(true);
            dispose();
        }
    }

    private void iniciarBloqueio() {
        btnEntrar.setEnabled(false);
        segundosRestantes = 180;

        timerBloqueio = new Timer(1000, e -> {
            segundosRestantes--;
            btnEntrar.setText("Aguarde (" + formatarTempo(segundosRestantes) + ")");
            if (segundosRestantes <= 0) {
                timerBloqueio.stop();
                btnEntrar.setText("Entrar");
                btnEntrar.setEnabled(true);
                tentativas = 0;
            }
        });
        timerBloqueio.start();

        JOptionPane.showMessageDialog(this,
                "Muitas tentativas incorretas!\nTente novamente em 3 minutos.",
                "Acesso Bloqueado", JOptionPane.WARNING_MESSAGE);
    }

    private String formatarTempo(int totalSegundos) {
        int min = totalSegundos / 60;
        int seg = totalSegundos % 60;
        return String.format("%02d:%02d", min, seg);
    }
}

class AppFrame extends JFrame {
    private final String tokenAutenticado = LoginFrame.token;

    public AppFrame() {
        carregarIcone();
        setTitle("Catch Services");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension tamanhoMinimo = new Dimension(1200, 800);
        setSize(1200, 800);
        setMinimumSize(tamanhoMinimo);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);

        JTabbedPane mainTabs = criarTabbedPanePrincipal();
        add(mainTabs);
    }

    private void carregarIcone() {
        try {
            URL url = getClass().getResource("/icones/logoCatch.png");
            if (url == null) {
                throw new FileNotFoundException("Ícone não encontrado: Catch.png");
            }

            Image icone = Toolkit.getDefaultToolkit().getImage(url);
            setIconImage(icone);
        } catch (Exception e) {
            System.out.println("Erro ao carregar ícone: " + e.getMessage());

        }
    }

    private JTabbedPane criarTabbedPanePrincipal() {
        JTabbedPane guia = new JTabbedPane();
        guia.setFont(FONT_LABEL);
        guia.setBackground(BACKGROUND);
        guia.setForeground(FOREGROUND);
        guia.setUI(new CustomTabbedPaneUI(BLUE_LIGHT, FOREGROUND));

        guia.addTab("Empresas", criarPainelEmpresas());
        guia.addTab("Usuarios", criarPainelUsuarios()); //colocar verificação de nivel do usuario via api

        return guia;
    }

    private JPanel criarPainelUsuarios() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(BACKGROUND);

        JTabbedPane subTabs = new JTabbedPane();
        subTabs.setFont(FONT_LABEL);
        subTabs.setBackground(BACKGROUND);
        subTabs.setForeground(FOREGROUND);
        subTabs.setUI(new CustomTabbedPaneUI(BLUE_LIGHT, FOREGROUND));

        subTabs.addTab("Adicionar Usuario", criarPainelCriarUsuario());
        subTabs.addTab("Lista de Usuarios", criarPainelListaComBotoes("Lista de ", "Usuarios"));
        painel.add(subTabs, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelEmpresas() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(BACKGROUND);

        JTabbedPane subTabs = new JTabbedPane();
        subTabs.setFont(FONT_LABEL);
        subTabs.setBackground(BACKGROUND);
        subTabs.setForeground(FOREGROUND);
        subTabs.setUI(new CustomTabbedPaneUI(BLUE_LIGHT, FOREGROUND));

        subTabs.addTab("Adicionar Empresa", criarPainelCriarEmpresa());
        subTabs.addTab("Lista de Empresas", criarPainelListaComBotoes("Lista de Empresas ", "Clientes"));

        painel.add(subTabs, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelCriarEmpresa() {
        JPanel painel = new JPanel(new BorderLayout(0, 20));
        painel.setBackground(BACKGROUND);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BLUE_LIGHT, 2, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        painelTitulo.setBackground(BACKGROUND);

        JLabel titulo = new JLabel("Criar Empresa");
        titulo.setFont(FONT_TITLE);
        titulo.setForeground(BLUE_LIGHT);
        painelTitulo.add(titulo);

        painel.add(painelTitulo, BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNome = new JLabel("Nome da Empresa:");
        lblNome.setFont(FONT_LABEL);
        lblNome.setForeground(FOREGROUND);
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelCentral.add(lblNome, gbc);

        JTextField txtNome = new JTextField();
        estilizarTextField(txtNome);
        gbc.gridx = 1;
        painelCentral.add(txtNome, gbc);

        JLabel lblCnpj = new JLabel("CNPJ:");
        lblCnpj.setFont(FONT_LABEL);
        lblCnpj.setForeground(FOREGROUND);
        gbc.gridx = 0;
        gbc.gridy = 1;
        painelCentral.add(lblCnpj, gbc);

        final JFormattedTextField txtCnpj;
        txtCnpj = new JFormattedTextField();
        estilizarTextField(txtCnpj);

        try {
            MaskFormatter mask = new MaskFormatter("##.###.###/####-##");
            mask.setPlaceholderCharacter('_');
            mask.setOverwriteMode(true);
            mask.setValidCharacters("0123456789");
            mask.setCommitsOnValidEdit(true);
            mask.install(txtCnpj);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        gbc.gridx = 1;
        painelCentral.add(txtCnpj, gbc);

        JLabel lblSenha = new JLabel("Senha STM:");
        lblSenha.setFont(FONT_LABEL);
        lblSenha.setForeground(FOREGROUND);
        gbc.gridx = 0;
        gbc.gridy = 2;
        painelCentral.add(lblSenha, gbc);

        JTextField txtSenha = new JTextField();
        estilizarTextField(txtSenha);
        gbc.gridx = 1;
        painelCentral.add(txtSenha, gbc);

        JLabel lblRegime = new JLabel("Regime Tributário:");
        lblRegime.setFont(FONT_LABEL);
        lblRegime.setForeground(FOREGROUND);
        gbc.gridx = 0;
        gbc.gridy = 3;
        painelCentral.add(lblRegime, gbc);

        String[] regimes = {"Simples Nacional", "Lucro Presumido", "Lucro Real", "MEI"};
        JComboBox<String> cbRegime = new JComboBox<>(regimes);
        cbRegime.setPreferredSize(new Dimension(330, 40));
        estilizarComboBox(cbRegime);
        gbc.gridx = 1;
        painelCentral.add(cbRegime, gbc);

        JPanel wrapperCentral = new JPanel(new GridBagLayout());
        wrapperCentral.setBackground(BACKGROUND);
        wrapperCentral.add(painelCentral);

        painel.add(wrapperCentral, BorderLayout.CENTER);

        JPanel pnlBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlBotoes.setBackground(BACKGROUND);

        ImageIcon iconeSalvar = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icones/salvar.png")));
        JButton btnSalvar = criarBotaoEstilizado("Salvar", iconeSalvar);
        btnSalvar.addActionListener(e -> {
            EmpresaDTO empresaNova = new EmpresaDTO();
            empresaNova.setNome(txtNome.getText());
            empresaNova.setCnpj(txtCnpj.getText());
            empresaNova.setSenhaStm(txtSenha.getText());
            empresaNova.setRegime(cbRegime.getSelectedItem().toString());

            if (criarEmpresaApi(empresaNova).equals("OK")) {
                txtNome.setText("");
                txtCnpj.setText("");
                txtSenha.setText("");
                cbRegime.setSelectedIndex(0);
                pegarValoresApi("Clientes");
            }

        });

        JButton btnCancelar = criarBotaoEstilizado("Cancelar", null);
        btnCancelar.addActionListener(e -> {
            txtNome.setText("");
            txtCnpj.setText("");
            txtSenha.setText("");
            cbRegime.setSelectedIndex(0);

        });

        pnlBotoes.add(btnSalvar);
        pnlBotoes.add(btnCancelar);
        painel.add(pnlBotoes, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelCriarUsuario() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(BACKGROUND);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BLUE_LIGHT, 2, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JTextField txtNome, txtEmail, txtUsuario, txtSenha;
        JFormattedTextField txtCpf;
        JComboBox<String> cbNivel;

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        // Título
        JLabel lblTitulo = new JLabel("Cadastro de Usuário");
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(BLUE_LIGHT);
        lblTitulo.setHorizontalAlignment(SwingConstants.LEFT);
        painel.add(lblTitulo, BorderLayout.NORTH);

        // Painel central com os campos
        JPanel pnlCampos = new JPanel();
        pnlCampos.setLayout(new GridBagLayout());
        pnlCampos.setBackground(BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 16, 16, 16);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Configuração dos campos
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlCampos.add(criarLabel("Nome:"), gbc);
        gbc.gridx = 1;
        txtNome = criarTextField();
        estilizarTextField(txtNome);
        pnlCampos.add(txtNome, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        pnlCampos.add(criarLabel("CPF:"), gbc);
        gbc.gridx = 1;
        txtCpf = new JFormattedTextField();
        estilizarTextField(txtCpf);
        try {
            MaskFormatter mask = new MaskFormatter("###.###.###-##");
            mask.setPlaceholderCharacter('_');
            mask.setOverwriteMode(true);
            mask.setValidCharacters("0123456789");
            mask.setCommitsOnValidEdit(true);
            mask.install(txtCpf);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        pnlCampos.add(txtCpf, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        pnlCampos.add(criarLabel("E-mail:"), gbc);
        gbc.gridx = 1;
        txtEmail = criarTextField();
        estilizarTextField(txtEmail);
        pnlCampos.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        pnlCampos.add(criarLabel("Usuário:"), gbc);
        gbc.gridx = 1;
        txtUsuario = criarTextField();
        estilizarTextField(txtUsuario);
        pnlCampos.add(txtUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        pnlCampos.add(criarLabel("Senha:"), gbc);
        gbc.gridx = 1;
        txtSenha = criarTextField();
        estilizarTextField(txtSenha);
        pnlCampos.add(txtSenha, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        pnlCampos.add(criarLabel("Nível:"), gbc);
        gbc.gridx = 1;
        String[] niveis = {"Administrador", "Operador"};
        cbNivel = new JComboBox<>(niveis);
        cbNivel.setPreferredSize(new Dimension(330, 40));
        estilizarComboBox(cbNivel);
        pnlCampos.add(cbNivel, gbc);

        painel.add(pnlCampos, BorderLayout.CENTER);

        // Painel de botões
        JPanel pnlBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlBotoes.setBackground(BACKGROUND);

        ImageIcon iconeSalvar = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icones/salvar.png")));
        JButton btnSalvar = criarBotaoEstilizado("Salvar", iconeSalvar);

        btnSalvar.addActionListener(ae -> {
            UsuarioDTO novoUsuario = new UsuarioDTO();
            novoUsuario.setNome(txtNome.getText());
            novoUsuario.setCpf(txtCpf.getText());
            novoUsuario.setEmail(txtEmail.getText());
            novoUsuario.setLogin(txtUsuario.getText());
            novoUsuario.setSenha(txtSenha.getText());

            if (criarUsuarioApi(novoUsuario).equals("OK")) {
                txtNome.setText("");
                txtCpf.setText("");
                txtEmail.setText("");
                txtUsuario.setText("");
                txtSenha.setText("");
                cbNivel.setSelectedIndex(0);
                pegarValoresApi("Usuarios");
            }
        });

        JButton btnCancelar = criarBotaoEstilizado("Limpar", null);
        btnCancelar.addActionListener(ae -> {
            txtNome.setText("");
            txtCpf.setText("");
            txtEmail.setText("");
            txtUsuario.setText("");
            txtSenha.setText("");
            cbNivel.setSelectedIndex(0);
        });

        pnlBotoes.add(btnSalvar);
        pnlBotoes.add(btnCancelar);
        painel.add(pnlBotoes, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelListaComBotoes(String txtAuxiliar, String categoria) {
        JPanel painel = new JPanel(new BorderLayout(12, 12));
        painel.setBackground(BACKGROUND);

        JLabel label = new JLabel(txtAuxiliar + categoria);
        label.setFont(FONT_TITLE);
        label.setForeground(BLUE_LIGHT);
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        painel.add(label, BorderLayout.NORTH);

        DefaultTableModel modeloTabela = new DefaultTableModel();
        JTable tabela = new JTable(modeloTabela);

        configurarTabela(tabela, categoria);

        JTableHeader header = tabela.getTableHeader();
        header.setBackground(BLUE_LIGHT);
        header.setForeground(FOREGROUND);
        header.setFont(FONT_TITLE.deriveFont(Font.BOLD));

        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI(BLUE_LIGHT));
        painel.add(scrollPane, BorderLayout.CENTER);

        switch (label.getText()) {
            case ("Lista de Usuarios") -> {
                modeloTabela.setColumnIdentifiers(new String[]{"ID", "NOME", "CPF", "EMAIL", "USUARIO", "IDB"});
                tabelaUsuarios = tabela;
                modeloUsuarios = modeloTabela;

                pegarValoresApi("Usuarios");
            }

            case ("Lista de Empresas Clientes") -> {
                modeloTabela.setColumnIdentifiers(new String[]{"ID", "NOME", "CNPJ", "REGIME", "SENHA STM", "IDB"});
                tabelaClientes = tabela;
                modeloClientes = modeloTabela;

                pegarValoresApi("Clientes");
            }

        }

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        painelBotoes.setBackground(BACKGROUND);

        ImageIcon iconeExcluir = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icones/lixeira.png")));
        JButton btnExcluirEmpresa = criarBotaoEstilizado("Excluir", iconeExcluir);

        btnExcluirEmpresa.addActionListener(ae -> {
            int[] linhasSelecionadas = tabela.getSelectedRows();
            for (int linha : linhasSelecionadas) {
                Object id = tabela.getValueAt(linha, 5);

                switch (label.getText()) {
                    case ("Lista de Usuarios") -> {
                        excluirUsuarioAPI((Long) id);
                        pegarValoresApi("Usuarios");
                    }

                    case ("Lista de Empresas Clientes") -> {
                        excluirEmpresaClienteAPI((Long) id);
                        pegarValoresApi("Clientes");
                    }

                }
            }

        });


        ImageIcon iconeAtualizar = new ImageIcon(Objects.requireNonNull(getClass().getResource("/icones/atualizar.png")));
        JButton btnRecarregarEmpresas = criarBotaoEstilizado("Atualizar", iconeAtualizar);

        btnRecarregarEmpresas.addActionListener(ae -> {
            switch (label.getText()) {
                case ("Lista de Usuarios") -> {
                    pegarValoresApi("Usuarios");
                }

                case ("Lista de Empresas Clientes") -> {
                    pegarValoresApi("Clientes");
                }
            }
        });

        painelBotoes.add(btnRecarregarEmpresas);
        painelBotoes.add(btnExcluirEmpresa);

        painel.add(painelBotoes, BorderLayout.SOUTH);

        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BLUE_LIGHT, 2, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        return painel;
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FONT_LABEL);
        return label;
    }

    private JTextField criarTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(330, 40));
        return textField;
    }

    // Funções da api

    private void pegarValoresApi(String classeAlvo) {
        if (classeAlvo.equals("Usuarios")) {
            int i = 0;
            try {
                java.util.List<Usuario> usuarios = ApiService.pegarUsuarios(tokenAutenticado);
                modeloUsuarios.setRowCount(0);

                for (Usuario usuario : usuarios) {
                    modeloUsuarios.addRow(new Object[]{
                            ++i,
                            usuario.getNome(),
                            usuario.getCpf(),
                            usuario.getEmail(),
                            usuario.getLogin(),
                            usuario.getIdUsuario()
                    });
                }

                configurarTabela(tabelaUsuarios, classeAlvo);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (classeAlvo.equals("Clientes")) {
            int i = 0;
            try {
                java.util.List<Empresa> clientes = ApiService.pegarClientes(tokenAutenticado);
                modeloClientes.setRowCount(0);

                for (Empresa cliente : clientes) {
                    modeloClientes.addRow(new Object[]{
                            ++i,
                            cliente.getNome(),
                            cliente.getCnpj(),
                            cliente.getRegime(),
                            cliente.getSenhaSTM(),
                            cliente.getIdEmpresaCliente()
                    });
                }
                configurarTabela(Tabelas.tabelaClientes, classeAlvo);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private String criarUsuarioApi(UsuarioDTO usuario) {

        String nome = usuario.getNome();
        String cpf = usuario.getCpf();
        String email = usuario.getEmail();
        String login = usuario.getLogin();
        String senha = usuario.getSenha();

        if (nome.isEmpty() || cpf.isEmpty() || senha.isEmpty() || login.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Campo Faltando, por favor verifique novamente",
                    "Campo faltando", JOptionPane.ERROR_MESSAGE);
            return "";
        }

        String mensagemConfirmacao =
                "Nome: " + nome + "\n" +
                        "CPF: " + cpf + "\n" +
                        "Email: " + email + "\n" +
                        "Login: " + login + "\n" +
                        "Senha: " + senha;


        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                mensagemConfirmacao,
                "Confirme os Dados do Usuário",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacao == JOptionPane.OK_OPTION) {
            try {
                Services.criarUsuario(usuario, tokenAutenticado);
                JOptionPane.showMessageDialog(this, "Usuário criado com sucesso!");
                return "OK";
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return "ERRO";
            }
        }
        return "";

    }

    private String criarEmpresaApi(EmpresaDTO empresa) {
        String nome = empresa.getNome();
        String cnpj = empresa.getCnpj();
        String senhaSTM = empresa.getSenhaStm();
        String regime = empresa.getRegime();

        if (nome.isEmpty() || cnpj.isEmpty() || senhaSTM.isEmpty() || regime.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Campo Faltando, por favor verifique novamente",
                    "Campo faltando", JOptionPane.ERROR_MESSAGE);
            return "";
        }

        String mensagemConfirmacao =
                "Nome: " + nome + "\n" +
                        "Cnpj: " + cnpj + "\n" +
                        "SenhaSTM: " + senhaSTM + "\n" +
                        "Regime: " + regime;


        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                mensagemConfirmacao,
                "Confirme os dados da Empresa",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacao == JOptionPane.OK_OPTION) {
            try {
                Services.criarEmpresaCliente(empresa, tokenAutenticado);
                JOptionPane.showMessageDialog(this, "Empresa criada com sucesso!");
                return "OK";
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return "ERRO";
            }
        }
        return "";
    }

    private void excluirUsuarioAPI(Long id) {
        try {
            Services.excluirUsuario(id, tokenAutenticado);
            JOptionPane.showMessageDialog(this, "Usuário excluido com Sucesso!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao Excluir: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirEmpresaClienteAPI(Long id) {
        try {
            Services.excluirEmpresaCliente(id, tokenAutenticado);
            JOptionPane.showMessageDialog(this, "Empresa Cliente excluido com Sucesso!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao Excluir: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarUsuariosAPI(JTable tabela, int linha, int coluna) {

        int modelRow = tabela.convertRowIndexToModel(linha);

        String novoValor = tabela.getValueAt(modelRow, coluna).toString();
        String nomeColuna = tabela.getModel().getColumnName(coluna);
        Long id = (Long) tabela.getModel().getValueAt(modelRow, 5);


        UsuarioDTO usuarioAtualizado = new UsuarioDTO();
        switch (nomeColuna) {
            case "NOME" -> usuarioAtualizado.setNome(novoValor);

            case "CPF" -> usuarioAtualizado.setCpf(novoValor);

            case "EMAIL" -> usuarioAtualizado.setEmail(novoValor);

            case "USUARIO" -> usuarioAtualizado.setLogin(novoValor);
        }
        try {
            ApiService.editarUsuario(id, usuarioAtualizado, tokenAutenticado);
            JOptionPane.showMessageDialog(this, "Usuário atualizado com Sucesso!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao Atualizar: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarClientesAPI(JTable tabela, int rowIndex, int columnIndex) {

        String novoValor = tabela.getValueAt(rowIndex, columnIndex).toString();
        String nomeColuna = tabela.getModel().getColumnName(columnIndex);
        Long id = (Long) tabela.getModel().getValueAt(rowIndex, 5);

        EmpresaClienteDTO clienteAtualizado = new EmpresaClienteDTO();
        switch (nomeColuna) {
            case "NOME" -> {
                clienteAtualizado.setNome(novoValor);
            }
            case "CNPJ" -> {
                clienteAtualizado.setCnpj(novoValor);
            }
            case "REGIME" -> {
                clienteAtualizado.setRegime(novoValor);
            }
            case "SENHA STM" -> {
                clienteAtualizado.setSenhaStm(novoValor);
            }
        }

        try {
            ApiService.editarEmpresaCliente(id, clienteAtualizado, tokenAutenticado);
            JOptionPane.showMessageDialog(this, "Usuário atualizado com Sucesso!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao Atualizar: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }

    }

    // Estilização dos componentes

    private void estilizarTextField(JTextField tf) {
        tf.setBackground(new Color(30, 30, 30));
        tf.setForeground(FOREGROUND);
        tf.setPreferredSize(new Dimension(330, 40));
        tf.setCaretColor(FOREGROUND);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BLUE_LIGHT, 2, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        tf.setFont(FONT_LABEL);
    }

    private void estilizarComboBox(JComboBox<?> cb) {
        cb.setBackground(BACKGROUND);
        cb.setForeground(FOREGROUND1);
        cb.setFont(FONT_LABEL);
        cb.setBorder(BorderFactory.createLineBorder(BLUE_LIGHT, 2, true));
        cb.setFocusable(false);
    }

    private void estilizarSpinner(JSpinner spinner, int tamanho) {
        spinner.getEditor().getComponent(0).setFont(FONT_LABEL);
        Dimension d = new Dimension(tamanho, 38);
        spinner.setPreferredSize(d);
        spinner.getEditor().getComponent(0).setBackground(BACKGROUND);
        spinner.getEditor().getComponent(0).setForeground(FOREGROUND);
        spinner.setBorder(BorderFactory.createLineBorder(BLUE_LIGHT, 2, true));
    }

    private JButton criarBotaoEstilizado(String texto, ImageIcon icone) {
        JButton btn = new JButton(texto) {
            final int marginHoriz = 10;
            final int marginVert = 5;

            @Override
            public Dimension getPreferredSize() {
                Dimension pref = super.getPreferredSize();
                return new Dimension(
                        Math.max(pref.width + 2 * marginHoriz, 30),
                        Math.max(pref.height + 2 * marginVert, 10)
                );
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);

            }
        };

        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setFont(FONT_LABEL);
        btn.setForeground(FOREGROUND);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (texto.equals("Excluir") || texto.equals("Cancelar")) {
            btn.setBackground(RED);
            btn.setBorder(new RoundedBorder(20, Cores.RED_BORDER));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(RED.darker());
                    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(RED);
                }

                public void mousePressed(java.awt.event.MouseEvent evt) {
                    btn.setBackground(RED.darker().darker());
                }

                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    btn.setBackground(RED);
                }
            });
        } else {
            btn.setBackground(BLUE_LIGHT);
            btn.setBorder(new RoundedBorder(20, BLUE_DARK));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(BLUE_DARK);
                    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(BLUE_LIGHT);
                }

                public void mousePressed(java.awt.event.MouseEvent evt) {
                    btn.setBackground(DODGER_BLUE);
                }

                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    btn.setBackground(BLUE_LIGHT);
                }
            });
        }

        if (icone != null) {
            Image img = icone.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(img));
            btn.setHorizontalTextPosition(SwingConstants.TRAILING);
            btn.setVerticalTextPosition(SwingConstants.CENTER);
            btn.setIconTextGap(5);
        }

        return btn;

    }

    private void configurarTabela(JTable tabela, String classeAlvo) {
        tabela.setFont(FONT_LABEL);
        tabela.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tabela.setBackground(new Color(30, 30, 30));
        tabela.setForeground(FOREGROUND);
        tabela.setRowHeight(40);
        tabela.setBorder(BorderFactory.createLineBorder(BLUE_LIGHT, 1, true));
        tabela.setShowGrid(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        for (int column = 0; column < tabela.getColumnCount(); column++) {
            TableColumn tableColumn = tabela.getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();

            for (int row = 0; row < tabela.getRowCount(); row++) {
                TableCellRenderer cellRenderer = tabela.getCellRenderer(row, column);
                Component c = tabela.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + tabela.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);

                if (preferredWidth >= maxWidth) {
                    preferredWidth = maxWidth;
                    break;
                }
            }
            tableColumn.setPreferredWidth(preferredWidth + 10);
        }
    }

    static class RoundedBorder extends AbstractBorder {

        private final int radius;
        private final Color color;
        private final BasicStroke stroke;

        public RoundedBorder(int radius, Color color) {
            this(radius, color, 1.0f); // Espessura padrão de 1px
        }

        public RoundedBorder(int radius, Color color, float thickness) {
            this.radius = radius;
            this.color = color;
            this.stroke = new BasicStroke(thickness);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(stroke);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = radius / 2;
            insets.top = insets.bottom = radius / 2;
            return insets;
        }

    }

    static class CustomScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {

        private final Color thumbColor;

        public CustomScrollBarUI(Color thumbColor) {
            this.thumbColor = thumbColor;
        }

        @Override
        protected void configureScrollBarColors() {
            this.trackColor = new Color(40, 40, 40);
            this.thumbHighlightColor = thumbColor.brighter();
            this.thumbDarkShadowColor = thumbColor.darker();
            this.thumbLightShadowColor = thumbColor.brighter();
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            btn.setMinimumSize(new Dimension(0, 0));
            btn.setMaximumSize(new Dimension(0, 0));
            return btn;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new Color(40, 40, 40));
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }
    }

    static class CustomTabbedPaneUI extends javax.swing.plaf.basic.BasicTabbedPaneUI {

        private final Color selectedColor;
        private final Color foregroundColor;

        public CustomTabbedPaneUI(Color selectedColor, Color foregroundColor) {
            this.selectedColor = selectedColor;
            this.foregroundColor = foregroundColor;
        }

        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabAreaInsets.left = 10;
            tabInsets = new Insets(10, 15, 10, 15);
            selectedTabPadInsets = new Insets(2, 2, 2, 2);
        }

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement,
                                          int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2 = (Graphics2D) g.create();
            if (isSelected) {
                g2.setColor(selectedColor);
                g2.fillRoundRect(x + 2, y + 2, w - 5, h - 4, 2, 2);
            } else {
                g2.setColor(new Color(46, 46, 46));
                g2.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 12, 12);
            }
            g2.dispose();
        }

        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font,
                                 FontMetrics metrics, int tabIndex, String title,
                                 Rectangle textRect, boolean isSelected) {
            g.setFont(font);
            g.setColor(isSelected ? foregroundColor : new Color(180, 180, 180));
            g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
        }

        @Override
        protected void paintFocusIndicator(Graphics g, int tabPlacement,
                                           Rectangle[] rects, int tabIndex,
                                           Rectangle iconRect, Rectangle textRect, boolean isSelected) {
            // Não pinta foco para visual mais limpo
        }
    }
}

class Cores {
    public static final Color BACKGROUND = new Color(255, 255, 255);
    public static final Color FOREGROUND = Color.BLACK;
    public static final Color BLUE_LIGHT = new Color(0, 191, 255);
    public static final Color DODGER_BLUE = new Color(30, 144, 225);
    public static final Color BLUE_DARK = new Color(0, 128, 191);
    public static final Color RED = new Color(255, 59, 48);
    public static final Color RED_BORDER = new Color(204, 34, 27);
}

class Fontes {
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 18);

}

class Tabelas {
    public static JTable tabelaUsuarios;
    public static DefaultTableModel modeloUsuarios;

    public static JTable tabelaClientes;
    public static DefaultTableModel modeloClientes;

    public static JTable tabelaEmpresasSTM;
    public static DefaultTableModel modeloEmpresasSTM;

    public static JTable tabelaHistoricoSTM;
    public static DefaultTableModel modeloHistoricoSTM;

    public static JTable tabelaEmpresasFGTS;
    public static DefaultTableModel modeloEmpresasFGTS;

    public static JTable tabelaHistoricoFGTS;
    public static DefaultTableModel modeloHistoricoFGTS;

    public static JTable tabelaEmpresasCND;
    public static DefaultTableModel modeloEmpresasCND;

    public static JTable tabelaHistoricoCND;
    public static DefaultTableModel modeloHistoricoCND;
}