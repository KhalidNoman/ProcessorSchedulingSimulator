import javax.swing.*;
import java.awt.event.ActionListener;
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
            return letter + " " ;
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

    class Processing extends Thread{
        String current;
        int times = 0;
        Processing(){}
        public void run(){
            lblQueue.setText("Queue: " );
            lblCurrent.setText("Current: ");
            txtGantt.setText("");
            int i = 0, done = 0;
            do{
//                System.out.println("PQ : " + pq);
                System.out.println("MINE : " + myQueue);
                if (done < size && elements.get(done).getArrival() <= i ) {
                    txtGantt.append("\tADD " + elements.get(done).getLetter() + "\n");
                    System.out.println("\tADD " + elements.get(done).getLetter());
//                    pq.add(elements.get(done));
                    addToPQ(elements.get(done));
                    lblQueue.setText("Queue: " + myQueue.toString());
                    //elements.remove(0);
                    done++;
                }
//                element temp = pq.peek();
                element temp = myQueue.get(0);
                if(temp != null) {
                    if (temp.getBurst() > 0) {
//                        if(!temp.getLetter().equals(current)){
//                            current = temp.getLetter();
//                            times = 1;
//                        } else {
//                            System.out.println("HERE times: " + times + " quantum: " + txtQuantum.getValue() + " " + (times > ((int)txtQuantum.getValue()) -1 ));
//                            if(times > ((int)txtQuantum.getValue()) -1){
//                                element check = pq.peek();
//                                if(pq.peek().getPriority() == check.getPriority()) {
//                                    System.out.println("SWITCH : " + times);
//                                    pq.add(pq.remove());
//                                    times = 1;
//                                }
//                            }
//                            times++;
//                        }

//                        if(!temp.getLetter().equals(current)){
//                            current = temp.getLetter();
//                            times = 0;
//                        } else {
//                            if(times > (int)txtQuantum.getValue()-1 && myQueue.size() > 1){
////                                element check = pq.poll();
//                                element check = myQueue.get(1);
////                                System.out.println("CHECK: " + check);
//                                if(temp.getPriority() == check.getPriority()){
//                                    addToPQ(myQueue.remove(0));
////                                    System.out.println("pq before: " + pq);
////                                    pq.add(check);
////                                    System.out.println("pq after: " + pq);
//                                    times = 0;
//                                }
//                            }
//                        }
                        if(myQueue.size() > 1) {
                            if (myQueue.get(0).getPriority() == myQueue.get(1).getPriority() && times >= (int) txtQuantum.getValue()) {
                                element swap = myQueue.remove(0);
                                addToPQ(swap);
                                times = 0;
                            }
                        }

                        times++;
//                        temp = pq.peek();
                        temp = myQueue.get(0);
                        myQueue.get(0).setBurst(temp.getBurst()-1);
//                        pq.peek().setBurst(temp.getBurst() - 1);

                        lblCurrent.setText("Current: " + temp.getLetter());
//                        System.out.println((i + 1) + " " + pq.peek().getLetter() + " times "  + times);
                        System.out.println((i + 1) + " " + myQueue.get(0).getLetter() + " times "  + times);
//                        txtGantt.append((i + 1) + " " + pq.peek().getLetter() + "\n");
                        txtGantt.append((i + 1) + " " + myQueue.get(0).getLetter() + "\n");

                    } else {
//                        System.out.println("\tREMOVE " + pq.peek().getLetter());
                        System.out.println("\tREMOVE " + myQueue.get(0).getLetter());
//                        txtGantt.append("\tREMOVE " + pq.peek().getLetter() + "\n");
                        txtGantt.append("\tREMOVE " + myQueue.get(0).getLetter() + "\n");
//                        pq.peek().setBurst(i);
                        myQueue.get(0).setBurst(i);
//                        elements.set((int)(pq.peek().getLetter().charAt(0) - 65), pq.peek());
                        elements.set((int)(myQueue.get(0).getLetter().charAt(0) - 65), myQueue.get(0));
//                        pq.poll();
                        myQueue.remove(0);
//                        lblQueue.setText("Queue: " + pq.toString());
                        lblQueue.setText("Queue: " + myQueue.toString());
                        times = 0;
                        i--;
                    }
                } else System.out.println(i+1);
                i++;
                try {
                    Thread.sleep(sleepTime);
                } catch (Exception err) {};
            }while(!myQueue.isEmpty() || done != size);
            System.out.println(i);
            System.out.println(elements.toString());

            pq.clear();
            elements.clear();
            btnGo.setEnabled(true);
            txtQuantum.setEnabled(true);

        }
    }


    PriorityQueue<element> pq = new PriorityQueue<element>(100, new elementComparator());
    ArrayList<element> elements = new ArrayList<element>();
    ArrayList<element> myQueue = new ArrayList<element>();
    File selectedFile = new File("C:\\Users\\Khalid\\Desktop\\input.txt");
    Scanner sc;
    int size;
    int sleepTime = 1000;

    public void addToPQ(element temp){
        for(int i = 0; i < myQueue.size(); i++){
            if(myQueue.get(i).getPriority() > temp.getPriority()){
                myQueue.add(i, temp);
                return;
            }
        }
        myQueue.add(temp);
    }


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
    private JTextArea txtGantt;
    private JLabel lblQuantum;
    private JButton btnSlower;
    private JButton btnFaster;
    private JLabel lblTime;

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
                                elements.add(temp);
                            }
                            Processing pr = new Processing();
                            pr.start();
                            btnGo.setEnabled(false);
                            btnQuantum.setEnabled(false);
                            txtQuantum.setEnabled(false);


                        }
                    }catch(Exception err){};

                }
            }
        });

        btnSlower.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                sleepTime += 100;
                lblTime.setText((double)sleepTime/1000 + " Seconds");
            }
        });
        btnFaster.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                sleepTime -= 100;
                lblTime.setText((double)sleepTime/1000 + " Seconds");
            }
        });
    }

    public static void main(String[] args) throws Exception{
        JFrame frame = new scheduler("Scheduler");
        frame.setVisible(true);
    }



}
