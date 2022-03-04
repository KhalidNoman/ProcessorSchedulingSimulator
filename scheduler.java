import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.PriorityQueue;
import java.util.*;

public class scheduler extends JFrame {

    private static JFrame frame;
    private JPanel mainPanel;
    private JTextField txtPath;
    private JTextArea txtContent;
    private JButton btnGo;
    private JSpinner txtQuantum;
    private JPanel resultPanel;
    private JLabel lblQueue;
    private JLabel lblCurrent;
    private JPanel quantumPanel;
    private JLabel lblInput;
    private JPanel setupPanel;
    private JTextArea txtGantt;
    private JLabel lblQuantum;
    private JButton btnSlower;
    private JButton btnFaster;
    private JLabel lblTime;
    private JLabel lblFont;
    private JButton btnFont;
    private JTextArea txtQueue;
    private JTextArea txtCurrentProcess;
    private JLabel lblTurnaround;
    private JTextArea txtTrunaround;
    private JLabel lblAverage;

    PriorityQueue<element> pq = new PriorityQueue<element>(100, new elementComparator());
    ArrayList<element> elements = new ArrayList<element>();
    ArrayList<element> myQueue = new ArrayList<element>();
    File selectedFile;
    Scanner sc;
    int size, fontSize = 12;
    int sleepTime = 1000;
    String prev = "";

    class Processing extends Thread{
        int times = 0;
        Processing(){}
        public void run(){
            txtTrunaround.setText("");
            lblAverage.setText("Average Turnaround: ");
            lblQueue.setText("Queue: " );
            lblCurrent.setText("Current: ");
            txtGantt.setText("");
            int i = 0, done = 0;
            do{
                if (done < size && elements.get(done).getArrival() <= i ) {
                    txtGantt.append("\tADD " + elements.get(done).getLetter() + "\n");
                    txtGantt.setCaretPosition(txtGantt.getDocument().getLength());
                    System.out.println("\tADD " + elements.get(done).getLetter());
                    addToPQ(elements.get(done));
                    txtQueue.setText(stringifyQueue());
                    done++;
                }
                if(myQueue.size() > 0) {
                    element temp = myQueue.get(0);
                    if(!prev.equals(temp.getLetter()))
                        times = 0;
                    if (temp.getBurst() > 0) {
                        if(myQueue.size() > 1) {
                            if (myQueue.get(0).getPriority() == myQueue.get(1).getPriority()
                                    && times >= (int) txtQuantum.getValue()) {
                                element swap = myQueue.remove(0);
                                addToPQ(swap);
                                times = 0;
                                txtGantt.append("\tPreempt " + swap.getLetter() + "\n");
                                txtQueue.setText(stringifyQueue());
                            }
                        }

                        times++;
                        temp = myQueue.get(0);
                        myQueue.get(0).setBurst(temp.getBurst()-1);
                        txtCurrentProcess.setText(temp.getLetter());
                        System.out.println((i + 1) + " " + myQueue.get(0).getLetter() + " times "  + times);
                        txtGantt.append((i + 1) + " " + myQueue.get(0).getLetter() + "\n");
                        txtGantt.setCaretPosition(txtGantt.getDocument().getLength());
                        if(myQueue.get(0).getBurst() == 0){
                            System.out.println("\tREMOVE " + myQueue.get(0).getLetter());
                            txtGantt.append("\tREMOVE " + myQueue.get(0).getLetter() + "\n");
                            txtGantt.setCaretPosition(txtGantt.getDocument().getLength());
                            myQueue.get(0).setBurst(i+1);
                            elements.set((Integer.parseInt(myQueue.get(0).getLetter().substring(1))), myQueue.get(0));
                            myQueue.remove(0);
                            txtQueue.setText(stringifyQueue());
                            times = 0;
                        }


                    }
                    prev = temp.getLetter();
                } else {
                    txtGantt.append(i+1 + "\n");
                    txtGantt.setCaretPosition(txtGantt.getDocument().getLength());
                    System.out.println(i + 1);
                }
                i++;
                try {
                    Thread.sleep(sleepTime);
                } catch (Exception err) {};
            }while(!myQueue.isEmpty() || done != size);
            System.out.println(i);
            System.out.println(elements.toString());
            int total = 0;
            for(int j = 0; j < elements.size(); j++){
                element ta = elements.get(j);
                txtTrunaround.append(ta.getLetter() + " = " + ta.getBurst() + " - "
                        + ta.getArrival() + " = " + (ta.getBurst()-ta.getArrival()) + "\n");
                total += ta.getBurst() - ta.getArrival();
            }
            String avgPrint = String.format("Average Turnaround: \n%d / %d = %.2f",
                    total, elements.size(), (double)total/elements.size());
            lblAverage.setText(avgPrint);

            pq.clear();
            elements.clear();
            btnGo.setEnabled(true);
            txtQuantum.setEnabled(true);

        }
    }

    public void addToPQ(element temp){
        for(int i = 0; i < myQueue.size(); i++){
            if(myQueue.get(i).getPriority() > temp.getPriority()){
                myQueue.add(i, temp);
                return;
            }
        }
        myQueue.add(temp);
    }

    public String stringifyQueue(){
        String queue = "";
        for(int i = 0; i < myQueue.size(); i++){
            if(i % 4 == 0 && i > 0){
                queue = queue + "\n";
            }
            queue = queue + " " + myQueue.get(i);
        }
        return queue;
    }




    public scheduler(String title) throws Exception{
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.setMinimumSize(new Dimension(1400, 500));
        this.setExtendedState( this.getExtendedState()|JFrame.MAXIMIZED_BOTH );
        this.pack();

        SpinnerModel model = new SpinnerNumberModel(2, 1, 1000, 1);
        txtQuantum.setModel(model);


        txtPath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
                fc.setFileFilter(filter);


                int chosen = fc.showOpenDialog(mainPanel);
                txtContent.setText("");

                if(chosen == JFileChooser.APPROVE_OPTION){
                    selectedFile = fc.getSelectedFile();
                    if(selectedFile.getName().endsWith(".txt")) {
                        txtPath.setText(selectedFile.getAbsolutePath());
                        try {
                            sc = new Scanner(selectedFile);
                            while (sc.hasNext()) {
                                txtContent.setText(txtContent.getText() + sc.nextLine() + "\n");
                            }
                        } catch (Exception err) {};
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Please use a text file.",
                                "Invalid File Type",
                                JOptionPane.ERROR_MESSAGE);
                        selectedFile = null;
                        txtPath.setText("");
                    }
                }
            }
        });


        btnGo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(selectedFile != null){
                    if(btnGo.isEnabled()) {
                        try {
                                sc = new Scanner(selectedFile);
                                size = sc.nextInt();
                                if (size > 100) {
                                    System.out.println("Too many processes!");
                                } else {
                                    for (int i = 0; i < size; i++) {
                                        int arr = sc.nextInt(), prt = sc.nextInt(), brst = sc.nextInt();
                                        element temp = new element(arr, prt, brst, i);
                                        elements.add(temp);
                                    }
                                    Processing pr = new Processing();
                                    pr.start();
                                    btnGo.setEnabled(false);
                                    txtQuantum.setEnabled(false);

                                }

                        } catch (Exception err) {};
                    }
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "File is null.",
                            "Check Input File",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnSlower.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                sleepTime = Math.abs(sleepTime + 500);
                lblTime.setText((double)sleepTime/1000 + " Seconds");
            }
        });
        btnFaster.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                sleepTime = Math.abs(sleepTime - 500);
                lblTime.setText((double)sleepTime/1000 + " Seconds");
            }
        });


        btnFont.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fontSize == 12){
                    fontSize = 24;
                } else {
                    fontSize = 12;
                }
                lblFont.setText("Font size: " + fontSize + "pt");
                txtContent.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                txtGantt.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                txtPath.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                txtQuantum.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                lblCurrent.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                lblQueue.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                lblFont.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                lblTime.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                lblInput.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                lblQuantum.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                btnFaster.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                btnFont.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                btnGo.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                btnSlower.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                txtQueue.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                txtCurrentProcess.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                txtTrunaround.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                lblTurnaround.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
                lblAverage.setFont(new Font("TimesRoman", Font.BOLD, fontSize));
            }
        });

        txtContent.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>)
                            evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : droppedFiles) {
                        selectedFile = file;
                        if(selectedFile.getName().endsWith(".txt")) {
                            txtPath.setText(selectedFile.getAbsolutePath());
                            txtContent.setText("");
                            try {
                                sc = new Scanner(selectedFile);
                                while (sc.hasNext()) {
                                    txtContent.setText(txtContent.getText() + sc.nextLine() + "\n");
                                }
                            } catch (Exception err) {};
                        } else {
                            JOptionPane.showMessageDialog(frame,
                                    "Please use a text file.",
                                    "Invalid File Type",
                                    JOptionPane.ERROR_MESSAGE);
                            selectedFile = null;
                            txtPath.setText("");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        txtQueue.setBackground(null);
        txtCurrentProcess.setBackground(null);
        txtTrunaround.setBackground(null);
        txtQueue.setBorder(null);
        txtTrunaround.setBorder(null);

    }

    static class element{
        private int arrival;
        private int priority;
        private int burst;
        private String letter;

        public element(int arrival, int priority, int burst, int letter){
            this.arrival = arrival;
            this.priority = priority;
            this.burst = burst;
            this.letter = "P" + letter;
        }

        public int getArrival(){
            return arrival;
        }
        public int getPriority(){
            return priority;
        }
        public int getBurst(){
            return burst;
        }
        public String getLetter(){return letter;}
        public void setBurst(int burst){ this.burst = burst; }

        @Override
        public String toString() {
            return letter + "";
        }
    }

    class elementComparator implements Comparator<element>{
        public int compare(element e1, element e2){
            if(e1.getPriority() <= e2.getPriority()){
                return 1;
            } else {
                return -1;
            }
        }
    }

    public static void main(String[] args) throws Exception{
        frame = new scheduler("Scheduler");
        frame.setVisible(true);
    }
}
