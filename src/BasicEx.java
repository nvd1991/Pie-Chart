import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

class Surface extends JPanel implements ActionListener {
    private final int DELAY = 1;
    private Timer timer;
    private int currentAngle;
    private float[][] pointList;

    public Surface(){
        currentAngle = 0;
        //Set up list of endpoints [startAngle, Red color, green color, blue color]
        pointList = new float[][]{
                {90f, 229f/255, 71f/255, 24f/255, 1f},
                {-40f, 219f/255, 218f/255, 37f/255, 1f},
                {-100f, 84f/255, 173f/255, 55f/255, 1f},
                {-145f, 58f/255, 156f/255, 246f/255, 1f},
                {-190f, 91f/255, 228f/255, 163f/255, 1f},
                {-220f, 248f/255, 178f/255, 72f/255, 1f},
                {-245f, 246f/255, 87f/255, 114f/255, 1f},
                {-260f, 134f/255, 119f/255, 210f/255, 1f},
        };
        initTimer();
    }

    private void initTimer(){
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void drawArray(Graphics2D g2d, float[][] pointListArray){
        //Some magic numbers
        int w = getWidth();
        int h = getHeight();
        int diameter = 200;
        int x = (w - diameter)/2;
        int y = (h - diameter - 100)/2;

        //Draw code
        int pointListLength = pointListArray.length;
        if(pointListLength == 0){
            return;
        } else if (pointListLength == 1) {
            //if only one end point, draw normally
            g2d.setPaint(new Color(pointListArray[0][1], pointListArray[0][2], pointListArray[0][3], pointListArray[0][4]));
            g2d.fillArc(x, y, diameter, diameter, (int)pointListArray[0][0], currentAngle);
        } else {
            //if more than one end points, draw (n-1) arcs (fixed) and then draw the (nth) arc based on the currentAngle
            for(int i = 0; i < pointListLength - 1; i++){
                g2d.setPaint(new Color(pointListArray[i][1], pointListArray[i][2], pointListArray[i][3], pointListArray[i][4]));
                g2d.fillArc(x, y, diameter, diameter, (int)pointListArray[i][0], (int)(pointListArray[i+1][0] - pointListArray[i][0]));
            }
            g2d.setPaint(new Color(pointListArray[pointListLength - 1][1], pointListArray[pointListLength - 1][2], pointListArray[pointListLength - 1][3], pointListArray[pointListLength - 1][4]));
            g2d.fillArc(x, y, diameter, diameter, (int)pointListArray[pointListLength - 1][0], currentAngle - (int)(pointListArray[pointListLength - 1][0] - pointListArray[0][0]));
        }

        //modify currentAngle and stop timer when the whole circle is finished
        currentAngle--;
        if(currentAngle < -360){
            timer.stop();
        }
    }

    private void doDrawing(Graphics g){
        Graphics2D g2d = (Graphics2D) g.create();

        //Rendering hints
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        int w = getWidth();
        int h = getHeight();

        //Draw background
        g2d.setPaint(new Color((60f/255), (63f/255), (65f/255), 1f));
        g2d.fillRect(0,0, w, h);

        //Pass a subset of Endpoints to drawArray function based on the currentAngle
        int numberOfItemToPass = 0;

        for(int i = 0; i < pointList.length; i++){
            //Avoid out of bound (i+1), default numberOfItemToPass = length of array
            if(i == pointList.length - 1){
                numberOfItemToPass = i + 1;
                break;
            }
            //Pass the corresponding copy of endpoints array based on the currentAngle (currentAngle < next EndPoint - Original Endpoint)
            if(currentAngle > pointList[i+1][0] - pointList[0][0]){
                numberOfItemToPass = i + 1;
                break;
            }
        }

        float[][] newPointList = new float[numberOfItemToPass][pointList[0].length];
        System.arraycopy(pointList, 0, newPointList, 0, numberOfItemToPass);
        //draw Array
        drawArray(g2d, newPointList);

        g2d.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}

public class BasicEx extends JFrame {
    public BasicEx(){
        initUI();
    }

    private void initUI(){
        add(new Surface());
        setTitle("Points");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                BasicEx ex = new BasicEx();
                ex.setVisible(true);
            }
        });
    }
}