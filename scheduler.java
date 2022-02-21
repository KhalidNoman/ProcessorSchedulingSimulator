import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.PriorityQueue;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class scheduler extends JFrame {

    static class element{
        int arrival;
        int priority;
        int burst;
        String letter;

        public element(int arrival, int priority, int burst, int letter){
            this.arrival = arrival;
            this.priority = priority;
            this.burst = burst;
            this.letter = Character.toString((char)letter);
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
            return arrival + " " + priority + " " + burst + " " + letter;
        }
    }

    class Processing implements Runnable{
        Processing(){}
        public void run(){
            while(!pq.isEmpty()){
                element temp = pq.peek();
                if(temp.getBurst() != 0){
                    pq.peek().setBurst(temp.getBurst()-1);
                    lblCurrent.setText("Current: " + temp.getLetter());
                    SwingUtilities.updateComponentTreeUI(lblCurrent);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    }catch(Exception err){};
                } else{
                    pq.remove();
                }
                System.out.println(pq.peek());
            }
        }
    }


    PriorityQueue<element> pq = new PriorityQueue<element>(100,
            (element1, element2) -> Integer.compare(element1.getPriority(), element2.getPriority()));

    File selectedFile;
    Scanner sc;
    int size;

    private JPanel mainPanel;
    private JTextField txtPath;
    private JTextArea txtContent;
    private JButton btnQuantum;
    private JButton btnGo;
    private JSpinner txtQuantum;
    private JPanel resultPanel;
    private JLabel lblQueue;
    private JLabel lblCurrent;
    private JPanel quantumPanel;
    private JLabel lblInput;
    private JPanel setupPanel;

    public scheduler(String title) throws Exception{
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.setExtendedState( this.getExtendedState()|JFrame.MAXIMIZED_BOTH );
        this.pack();

        txtQuantum.setValue(2);

        txtPath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JFileChooser fc = new JFileChooser();
                int chosen = fc.showOpenDialog(mainPanel);

                if(chosen == JFileChooser.APPROVE_OPTION){
                    selectedFile = fc.getSelectedFile();
                    txtPath.setText(selectedFile.getAbsolutePath());
                    try {
                        sc = new Scanner(selectedFile);
                        while(sc.hasNext()){
                            txtContent.setText(txtContent.getText() + sc.nextLine() + "\n");
                        }
                    }catch(Exception err){};
                }
            }
        });


        btnGo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(selectedFile != null){
                    try{
                        sc = new Scanner(selectedFile);
                        size = sc.nextInt();
                        if(size > 100){
                            //something
                        } else {
                            for(int i  = 0; i < size; i++){
                                int arr = sc.nextInt(), prt = sc.nextInt(), brst = sc.nextInt();
                                element temp = new element(arr, prt, brst, i+65);
                                pq.add(temp);
                            }
                            System.out.println(pq.toString());
                            Processing pr = new Processing();
                            pr.run();


                        }
                    }catch(Exception err){};

                }
            }
        });
    }

    public static void main(String[] args) throws Exception{
        JFrame frame = new scheduler("Scheduler");
        frame.setVisible(true);
    }



}
