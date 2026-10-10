import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Study Planner - a Java Swing mini-project.
 * Starts empty. Tasks and study sessions are kept in memory for this run.
 */
public class StudyPlanner extends JFrame {
    private static final Color NAVY = new Color(31, 41, 71);
    private static final Color BLUE = new Color(76, 110, 245);
    private static final Color BG = new Color(246, 248, 252);
    private static final Color MUTED = new Color(105, 115, 135);

    private final List<StudyTask> tasks = new ArrayList<>();
    private final List<StudySession> sessions = new ArrayList<>();
    private final DefaultTableModel taskModel = new DefaultTableModel(new Object[]{"Done", "Task", "Subject"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return column == 0; }
        @Override public Class<?> getColumnClass(int columnIndex) { return columnIndex == 0 ? Boolean.class : String.class; }
    };
    private final DefaultTableModel sessionModel = new DefaultTableModel(new Object[]{"Time", "Study session"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private JTable taskTable;
    private JTable sessionTable;
    private JLabel totalValue, remainingValue, completedValue, progressValue;
    private JProgressBar progressBar;
    private JLabel subjectProgress;

    public StudyPlanner() {
        super("Study Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 650));
        setSize(1050, 760);
        setLocationRelativeTo(null);
        buildUI();
        refreshDashboard();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG);
        setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(18, 26, 18, 26));
        JLabel title = new JLabel("Study Planner");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        title.setForeground(NAVY);
        JLabel subtitle = new JLabel("Organize your tasks. Track your progress. Plan your study time.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);
        JPanel heading = new JPanel(); heading.setOpaque(false); heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(title); heading.add(Box.createVerticalStrut(5)); heading.add(subtitle);
        header.add(heading, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(22, 24, 24, 24));
        root.add(content, BorderLayout.CENTER);

        JPanel dashboard = new JPanel(new GridLayout(1, 4, 12, 0));
        dashboard.setOpaque(false);
        totalValue = new JLabel("0"); remainingValue = new JLabel("0"); completedValue = new JLabel("0"); progressValue = new JLabel("0%");
        dashboard.add(statCard("Total tasks", totalValue, new Color(235, 240, 255)));
        dashboard.add(statCard("Remaining", remainingValue, new Color(255, 245, 225)));
        dashboard.add(statCard("Completed", completedValue, new Color(225, 247, 237)));
        dashboard.add(statCard("Overall progress", progressValue, new Color(242, 235, 255)));

        JPanel upper = new JPanel(new BorderLayout(0, 12)); upper.setOpaque(false);
        upper.add(dashboard, BorderLayout.NORTH);
        JPanel progressPanel = cardPanel();
        progressPanel.setLayout(new BorderLayout(10, 8));
        JLabel progressTitle = new JLabel("Task progress"); progressTitle.setFont(new Font("SansSerif", Font.BOLD, 14)); progressTitle.setForeground(NAVY);
        progressBar = new JProgressBar(0, 100); progressBar.setStringPainted(true); progressBar.setForeground(BLUE); progressBar.setBackground(new Color(230, 234, 242));
        subjectProgress = new JLabel("Add tasks to see subject progress."); subjectProgress.setForeground(MUTED); subjectProgress.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JPanel progressInner = new JPanel(); progressInner.setOpaque(false); progressInner.setLayout(new BoxLayout(progressInner, BoxLayout.Y_AXIS));
        progressInner.add(progressTitle); progressInner.add(Box.createVerticalStrut(10)); progressInner.add(progressBar); progressInner.add(Box.createVerticalStrut(8)); progressInner.add(subjectProgress);
        progressPanel.add(progressInner, BorderLayout.CENTER);
        upper.add(progressPanel, BorderLayout.CENTER);
        content.add(upper, BorderLayout.NORTH);

        JPanel lower = new JPanel(new GridLayout(1, 2, 14, 0)); lower.setOpaque(false);
        lower.add(buildTasksPanel());
        lower.add(buildSessionsPanel());
        content.add(lower, BorderLayout.CENTER);
    }

    private JPanel statCard(String label, JLabel value, Color tint) {
        JPanel panel = new JPanel(); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230, 233, 240)), new EmptyBorder(15, 16, 15, 16)));
        JLabel name = new JLabel(label); name.setFont(new Font("SansSerif", Font.PLAIN, 12)); name.setForeground(MUTED);
        value.setFont(new Font("SansSerif", Font.BOLD, 26)); value.setForeground(NAVY);
        value.setOpaque(true); value.setBackground(tint); value.setBorder(new EmptyBorder(3, 9, 3, 9));
        panel.add(name); panel.add(Box.createVerticalStrut(10)); panel.add(value);
        return panel;
    }

    private JPanel buildTasksPanel() {
        JPanel panel = cardPanel(); panel.setLayout(new BorderLayout(0, 12));
        JPanel heading = sectionHeading("My Tasks", "Add tasks and mark them complete.");
        panel.add(heading, BorderLayout.NORTH);
        taskTable = new JTable(taskModel); styleTable(taskTable); taskTable.setRowHeight(30);
        taskTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 0 && e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                if (row >= 0 && row < tasks.size()) {
                    tasks.get(row).done = Boolean.TRUE.equals(taskModel.getValueAt(row, 0));
                    refreshDashboard();
                }
            }
        });
        JScrollPane scroll = new JScrollPane(taskTable); scroll.setBorder(BorderFactory.createLineBorder(new Color(232, 235, 242)));
        panel.add(scroll, BorderLayout.CENTER);
        JPanel controls = new JPanel(new BorderLayout(6, 5)); controls.setOpaque(false);
        JPanel fields = new JPanel(new GridLayout(2, 2, 6, 4)); fields.setOpaque(false);
        JTextField taskInput = new JTextField(); taskInput.setToolTipText("Enter a task name");
        JTextField subjectInput = new JTextField(); subjectInput.setToolTipText("Enter a subject (optional)");
        styleInput(taskInput); styleInput(subjectInput);
        JLabel taskNameLabel = new JLabel("Task name"); taskNameLabel.setForeground(Color.BLACK); JLabel subjectLabel = new JLabel("Subject (optional)"); subjectLabel.setForeground(Color.BLACK); fields.add(taskNameLabel); fields.add(subjectLabel);
        fields.add(taskInput); fields.add(subjectInput);
        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0)); actions.setOpaque(false);
        JButton add = primaryButton("Add task"); JButton remove = secondaryButton("Remove selected");
        actions.add(add); actions.add(remove);
        controls.add(fields, BorderLayout.CENTER); controls.add(actions, BorderLayout.SOUTH);
        add.addActionListener(e -> {
            String task = taskInput.getText().trim(); String subject = subjectInput.getText().trim();
            if (task.isEmpty()) { showError("Please enter a task name."); return; }
            tasks.add(new StudyTask(task, subject));
            taskModel.addRow(new Object[]{false, task, subject.isEmpty() ? "—" : subject});
            taskInput.setText(""); subjectInput.setText(""); refreshDashboard();
        });
        remove.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row < 0) { showError("Select a task to remove."); return; }
            tasks.remove(row); taskModel.removeRow(row); refreshDashboard();
        });
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildSessionsPanel() {
        JPanel panel = cardPanel(); panel.setLayout(new BorderLayout(0, 12));
        panel.add(sectionHeading("Study Schedule", "Add your own study sessions."), BorderLayout.NORTH);
        sessionTable = new JTable(sessionModel); styleTable(sessionTable); sessionTable.setRowHeight(30);
        JScrollPane scroll = new JScrollPane(sessionTable); scroll.setBorder(BorderFactory.createLineBorder(new Color(232, 235, 242)));
        panel.add(scroll, BorderLayout.CENTER);
        JPanel controls = new JPanel(new BorderLayout(6, 5)); controls.setOpaque(false);
        JPanel fields = new JPanel(new GridLayout(2, 2, 6, 4)); fields.setOpaque(false);
        JTextField nameInput = new JTextField(); JTextField timeInput = new JTextField(); timeInput.setToolTipText("24-hour time, for example 16:30");
        styleInput(nameInput); styleInput(timeInput);
        JLabel sessionNameLabel = new JLabel("Session name"); sessionNameLabel.setForeground(Color.BLACK); JLabel sessionTimeLabel = new JLabel("Time (HH:mm)"); sessionTimeLabel.setForeground(Color.BLACK); fields.add(sessionNameLabel); fields.add(sessionTimeLabel);
        fields.add(nameInput); fields.add(timeInput);
        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0)); actions.setOpaque(false);
        JButton add = primaryButton("Add session"); JButton remove = secondaryButton("Remove selected");
        actions.add(add); actions.add(remove);
        controls.add(fields, BorderLayout.CENTER); controls.add(actions, BorderLayout.SOUTH);
        add.addActionListener(e -> {
            String name=nameInput.getText().trim(), time=timeInput.getText().trim();
            if (name.isEmpty() || time.isEmpty()) { showError("Enter a session name and time."); return; }
            try {
                LocalTime parsed = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
                StudySession session = new StudySession(name, parsed);
                sessions.add(session); sessions.sort((a,b)->a.time.compareTo(b.time));
                rebuildSessionTable(); nameInput.setText(""); timeInput.setText("");
            } catch (Exception ex) { showError("Enter time in 24-hour HH:mm format, such as 09:30 or 16:00."); }
        });
        remove.addActionListener(e -> {
            int row=sessionTable.getSelectedRow(); if(row<0){showError("Select a session to remove.");return;}
            String name=(String)sessionModel.getValueAt(row,1); String time=(String)sessionModel.getValueAt(row,0);
            sessions.removeIf(s->s.name.equals(name) && s.time.format(DateTimeFormatter.ofPattern("HH:mm")).equals(time)); rebuildSessionTable();
        });
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private void rebuildSessionTable() {
        sessionModel.setRowCount(0);
        DateTimeFormatter fmt=DateTimeFormatter.ofPattern("HH:mm");
        for(StudySession s:sessions) sessionModel.addRow(new Object[]{s.time.format(fmt),s.name});
    }

    private void refreshDashboard() {
        int total=tasks.size(), completed=0; for(StudyTask t:tasks) if(t.done) completed++;
        int remaining=total-completed; int percent=total==0?0:(int)Math.round(completed*100.0/total);
        totalValue.setText(String.valueOf(total)); remainingValue.setText(String.valueOf(remaining)); completedValue.setText(String.valueOf(completed));
        progressValue.setText(percent+"%"); progressBar.setValue(percent); progressBar.setString(percent+"%");
        if(tasks.isEmpty()) { subjectProgress.setText("Add tasks to see subject progress."); return; }
        java.util.Map<String,int[]> bySubject=new java.util.LinkedHashMap<>();
        for(StudyTask t:tasks) { String key=t.subject.isEmpty()?"Unassigned":t.subject; int[] counts=bySubject.computeIfAbsent(key,k->new int[2]); counts[0]++; if(t.done)counts[1]++; }
        StringBuilder text=new StringBuilder("Subject progress: "); int n=0;
        for(java.util.Map.Entry<String,int[]> entry:bySubject.entrySet()) { if(n++>0)text.append("   •   "); int[] v=entry.getValue(); text.append(entry.getKey()).append(" ").append(v[1]).append("/").append(v[0]); }
        subjectProgress.setText(text.toString());
    }

    private JPanel cardPanel() { JPanel p=new JPanel(); p.setBackground(Color.WHITE); p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230,233,240)),new EmptyBorder(16,16,16,16))); return p; }
    private JPanel sectionHeading(String title, String subtitle) {
        JPanel p=new JPanel();p.setOpaque(false);p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        JLabel t=new JLabel(title);t.setFont(new Font("SansSerif",Font.BOLD,16));t.setForeground(NAVY);
        JLabel s=new JLabel(subtitle);s.setFont(new Font("SansSerif",Font.PLAIN,12));s.setForeground(MUTED);
        p.add(t);p.add(Box.createVerticalStrut(4));p.add(s);return p;
    }
    private void styleTable(JTable table) { table.setFont(new Font("SansSerif",Font.PLAIN,12)); table.setForeground(NAVY); table.setBackground(Color.WHITE); table.setGridColor(new Color(238,240,245)); table.setSelectionBackground(new Color(231,237,255)); table.setSelectionForeground(NAVY); table.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12)); table.getTableHeader().setBackground(new Color(247,248,251)); table.getTableHeader().setForeground(MUTED); }
    private void styleInput(JTextField field) { field.setForeground(Color.BLACK); field.setCaretColor(Color.BLACK); field.setBackground(Color.WHITE); field.setFont(new Font("SansSerif",Font.PLAIN,12)); field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(215,220,231)),new EmptyBorder(7,8,7,8))); }
    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(190, 215, 255));
        b.setForeground(Color.BLACK);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 110, 180), 1),
            new EmptyBorder(9, 13, 9, 13)));
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(120, 40));
        b.setMinimumSize(new Dimension(100, 38));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    private JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        // High-contrast destructive/action button: remains visible on Windows themes.
        b.setBackground(new Color(255, 210, 210));
        b.setForeground(Color.BLACK);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 90, 90), 1),
            new EmptyBorder(9, 13, 9, 13)));
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(140, 40));
        b.setMinimumSize(new Dimension(110, 38));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    private void showError(String message) { JOptionPane.showMessageDialog(this,message,"Study Planner",JOptionPane.INFORMATION_MESSAGE); }

    private static class StudyTask { String name, subject; boolean done; StudyTask(String name,String subject){this.name=name;this.subject=subject;this.done=false;} }
    private static class StudySession { String name; LocalTime time; StudySession(String name,LocalTime time){this.name=name;this.time=time;} }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new StudyPlanner().setVisible(true));
    }
}
