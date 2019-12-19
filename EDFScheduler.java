/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edf.scheduler;
/**
 *
 * @author M7md_
 */

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author M7md_Karam
 * 
 */

public class EDFScheduler extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        Process proc1 = new Process();
        Process proc2 = new Process();
        Process proc3 = new Process();
        BorderPane borderPane = new BorderPane();
        Line lP1 = new Line(20,100,980,100);
        Line lP2 = new Line(20,200,980,200);
        Line lP3 = new Line(20,300,980,300);
        
        Line l = new Line(20,400,980,400);
        Pane root = new Pane();
        TableGrid t = new TableGrid();
        Button btn = new Button("schedule");
        btn.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e){
                int p1,p2,p3,p4,p5,p6;
                Integer[] a = t.returnValues();
               p1= proc1.time = a[0];
               p2= proc2.time=  a[1];
               p3=proc3.time = a[2];
               p4= proc1.period = proc1.nextDeadLine= a[3];
               p5= proc2.period= proc2.nextDeadLine= a[4];
               p6= proc3.period =proc3.nextDeadLine= a[5];
               proc1.name ="P1";
               proc2.name ="P2";
               proc3.name ="P3";
               int lcm = calcLCM(p4,p5,p6);
               int sector = 960/lcm;
               int start = 20+sector;
               int i=0;
               Process fPirority,sPirority,thPirority;
                Process[] pir = calcPiriority(p4,p5,p6,proc1,proc2,proc3); 
                fPirority = pir[0];
                sPirority = pir[1];
                thPirority = pir[2];
                Process excutable = new Process();
                int fTime = fPirority.time;
                int sTime = sPirority.time;
                int thTime = thPirority.time;
                System.out.println("pro "+ proc3.nextDeadLine+" read "+ proc3.state);
               while(i<lcm){
                    Line l1 = new Line(start,395,start,405);
                    Rectangle r = new Rectangle();
                    Text txt = new Text(Integer.toString(i+1));
                    txt.setX(start);
                    txt.setY(420);
                    start +=sector;
                    
                    {
                    excutable = calEdf(proc1,proc2,proc3,i);
                    if(excutable==proc1){
                        fTime--;
                        if(fTime==0){
                                fTime = proc1.time;
                                proc1.nextDeadLine +=proc1.period; 
                                proc1.state = "finished";
                            }
                        root.getChildren().add(drawP1(sector,i));
                    }
                    else if(excutable==proc2){
                        sTime--;
                        if(sTime==0){
                                sTime = proc2.time;
                                proc2.nextDeadLine +=proc2.period; 
                                proc2.state = "finished";
                            }
                        root.getChildren().add(drawP2(sector,i));
                    }
                    else if(excutable==proc3){
                        thTime--;
                        if(thTime==0){
                                thTime = proc3.time;
                                proc3.nextDeadLine +=proc3.period;
                                proc3.state = "finished";
                            }
                        else{
                            thPirority.state = "executing";
                        }
                        root.getChildren().add(drawP3(sector,i));
                    }}
                    
                    i++;
                    if(i%proc1.period==0)
                        proc1.state = "ready";
                    if(i%proc2.period==0)
                        proc2.state = "ready";
                    if(i%proc3.period==0)
                        proc3.state = "ready";
                    root.getChildren().addAll(l1,txt);
               }
            }
            });
            
        root.getChildren().addAll(lP1,lP2,lP3,l);
        borderPane.setCenter(root);
        borderPane.setTop(t.drawTable());
        borderPane.setBottom(btn);
        Scene scene = new Scene(borderPane, 1000, 600);
        primaryStage.setTitle("EDF scheduling");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    public static void main(String[] args) {
        launch(args);
    }
    public Process calEdf(Process p1, Process p2, Process p3,int i){
        if((p1.nextDeadLine)<(p2.nextDeadLine)&&(p1.nextDeadLine)<(p3.nextDeadLine)&&p1.state=="ready")   return p1; 
        else if(p2.nextDeadLine<p1.nextDeadLine&&p2.nextDeadLine<p3.nextDeadLine&&p2.state=="ready")      return p2;        
        else if(p2.state != "ready"&&p1.state != "ready"&&(p3.state=="ready"||p3.state=="executing"))   {
            return p3;
        }       
        else if(p1.nextDeadLine==p2.nextDeadLine){
            if(p1.state=="ready"&&p2.state=="ready"){
                if(p1.pir<p2.pir)             return p1;
                else                          return p2;
            }
            else if((p1.state=="ready"||p1.state=="executing")&&p2.state=="finished") return p1;
            else if((p2.state=="ready"||p2.state=="executing")&&p1.state=="finished") return p2;
            else if(p1.state=="executing"&&p2.state=="ready")                         return p1;
            else if(p2.state=="executing"&&p1.state=="ready")                         return p2;
            else return null;
        }
        else if(p1.nextDeadLine==p3.nextDeadLine)
        {
            if(p1.state=="ready"&&p3.state=="ready"){
                if(p1.pir<p3.pir)              return p1;
                else                           return p3;
            }
            else if((p1.state=="ready"||p1.state=="executing")&&p3.state=="finished") return p1;
            else if((p3.state=="ready"||p3.state=="executing")&&p1.state=="finished") return p3;
            else if(p1.state=="executing"&&p3.state=="ready")                         return p1;
            else if(p3.state=="executing"&&p1.state=="ready")                         return p1;
            else return null;
        }
        else if(p2.nextDeadLine==p3.nextDeadLine)
        {
            if(p2.state=="ready"&&p3.state=="ready"){
                if(p2.pir<p3.pir)            return p2;
                else                         return p3;
            }
            else if((p2.state.equals("ready")||p1.state.equals("executing"))&&p3.state.equals("finished")) return p2;
            else if((p3.state=="ready"||p3.state=="executing")&&p2.state=="finished") return p3;
            else if(p2.state=="executing"&&p3.state=="ready")                         return p2;
            else if(p3.state=="executing"&&p2.state=="ready")                         return p2;
            else  return null;
        }
        else      return null;
    }
    public Rectangle drawP1(int sector, int i){
        Rectangle r = new Rectangle();
        r.setFill(Color.RED);
        r.setX(20+sector*i);
                        r.setY(70);
                        r.setWidth(sector);
                        r.setHeight(30);
                        return r;
                        }
    public Rectangle drawP2(int sector, int i){
        Rectangle r = new Rectangle();
        r.setFill(Color.BLUE);
        r.setX(20+sector*i);
        r.setY(170);
        r.setWidth(sector);
        r.setHeight(30);
        return r;
        }
    public Rectangle drawP3(int sector,int i){
        Rectangle r = new Rectangle();
        r.setFill(Color.AQUA);
        r.setX(20+sector*i);
        r.setY(270);
        r.setWidth(sector);
        r.setHeight(30);
        return r;
    }
    public int calcLCM(int i, int j, int k){
        int lcm=0;
        if(i%j==0&&i%k==0)
                   lcm= i;
               else if(j%i==0&&j%k==0)
                   lcm=j;
               else if(k%j==0&&k%i==0)
                   lcm=k;
               else if((i*j)%k==0)
                   lcm=i*j;
               else if((j*k)%i==0)
                   lcm=j*k;
               else if((i*k)%j==0)
                   lcm=i*k;
               else 
                   lcm=j*k*i;
        return lcm;
    }
   public Process[] calcPiriority(int p4, int p5, int p6,Process proc1,Process proc2, Process proc3){
        Process [] pir = new Process[3];
        if(p4<p5&&p5<p6){
             pir[0] = proc1;           
             pir[1] = proc2;
             pir[2] = proc3;
             proc1.pir=1;
             proc2.pir=2;
             proc3.pir=3;
        }
       else if(p4<p6&&p6<p5){
            pir[0] = proc1;
            pir[1] = proc3;
            pir[2] =proc2;
            proc1.pir=1;
            proc3.pir=2;
            proc2.pir=3;
        }
       else if(p5<p4&&p4<p6){
            pir[0] = proc2;
            pir[1] = proc1;
            pir[2] = proc3;
            proc2.pir=1;
            proc1.pir=2;
            proc3.pir=3;
        }
       else if(p5<p6&&p6<p4){
            pir[0] = proc2;
            pir[1] = proc3;
            pir[2] = proc1;
            proc2.pir=1;
            proc3.pir=2;
            proc1.pir=3;
            }
          else if(p6<p4&&p4<p5){
                        pir[0]= proc3;
                        pir[1] = proc1;
                        pir[2]= proc2;
                        proc3.pir=1;
            proc1.pir=2;
            proc2.pir=3;
                    }
                    else if(p6<p5&&p5<p4){
                        pir[0]= proc3;
                        pir[1]= proc2;
                        pir[2]= proc1;
                        proc3.pir=1;
            proc2.pir=2;
            proc1.pir=3;
                    }
       return pir;
    } 
}
class TableGrid extends GridPane{
    Integer[] a = new Integer[6];
    TextField tf1 = new TextField();
        TextField tf2 = new TextField();
        TextField tf3 = new TextField();
        TextField tf4 = new TextField();
        TextField tf5 = new TextField();
        TextField tf6 = new TextField();
    public GridPane drawTable(){
       this.setAlignment(Pos.CENTER);
        
        this.add(new Text("Execution Time"),1,0);
        this.add(new Text("Period"),2,0);
        this.add(new Text("P1"),0,1);
        this.add(new Text("P2"),0,2);
        this.add(new Text("P3"),0,3);
        this.add(tf1,1,1);
        this.add(tf2,1,2);
        this.add(tf3,1,3);
        this.add(tf4,2,1);
        this.add(tf5,2,2);
        this.add(tf6,2,3);
        return this;
    }
    public Integer[] returnValues(){
        
             a[0] = Integer.parseInt(tf1.getText());
             a[1] = Integer.parseInt(tf2.getText());
             a[2] = Integer.parseInt(tf3.getText());
             a[3] = Integer.parseInt(tf4.getText());
             a[4] = Integer.parseInt(tf5.getText());
             a[5] = Integer.parseInt(tf6.getText());
        
        return a;
    }    
}
class Process{
    int time;
    int period;
    String name;
    String  state = "ready";
    int nextDeadLine ;
    int pir;
}