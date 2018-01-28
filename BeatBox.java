import javax.sound.midi.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class BeatBox{

JPanel majorPanel;
ArrayList<JCheckBox>checkboxList;
Sequencer sequencer;
Sequence sequence;
Track track;
JFrame frame;

String[] instrumentName={"Bass Drum","Closed Hi-Hat", "Open Hi-Hat","Acoustic Snare", "Crash Cymbal","Hand Clap","High Tom","Hi Bongo","Marcas","Whistle","Low Conga","Cowbell","Vibraslap","Low-mid Tom","High Agogo","Open Hi Conga"};
int[] instruments={35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};


public static void main(String [] args)
{
new BeatBox().buildGui();
}

public void buildGui(){
frame=new JFrame("Cyber BeatBox");
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
BorderLayout blayout=new BorderLayout();
JPanel bkgrnd=new JPanel(blayout);
bkgrnd.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

checkboxList=new ArrayList<>();
Box btnBox=new Box(BoxLayout.Y_AXIS);

JButton start=new JButton("Start");
start.addActionListener(new MyStartListener());
btnBox.add(start);

JButton stop=new JButton("Stop");
start.addActionListener(new MyStopListener());
btnBox.add(stop);

JButton upTempo=new JButton("Tempo Up");
start.addActionListener(new MyUpTempoListener());
btnBox.add(upTempo);

JButton downTempo=new JButton("Tempo Down");
start.addActionListener(new MyDownTempoListener());
btnBox.add(downTempo);

Box nameBox=new Box(BoxLayout.Y_AXIS);
for(int i=0;i<16;i++){
nameBox.add(new Label(instrumentName[i]));
}

bkgrnd.add(BorderLayout.EAST,btnBox);
bkgrnd.add(BorderLayout.WEST,nameBox);

frame.getContentPane().add(bkgrnd);

GridLayout grid=new GridLayout(16,16);
grid.setVgap(1);
grid.setHgap(2);
majorPanel=new JPanel(grid);
bkgrnd.add(BorderLayout.CENTER,majorPanel);

for(int i=0;i<256;i++){
JCheckBox c=new JCheckBox();
c.setSelected(false);
checkboxList.add(c);
majorPanel.add(c);
}
setUpMidi();

frame.setBounds(50,50,300,300);
frame.pack();
frame.setVisible(true);
}


public void setUpMidi(){
try
{
sequencer=MidiSystem.getSequencer();
sequencer.open();
sequence=new Sequence(Sequence.PPQ,4);
track=sequence.createTrack();
sequencer.setTempoInBPM(120);
}
catch(Exception ex){ex.printStackTrace();}
}


public MidiEvent makeEvent(int comd,int chan,int one,int two,int tick)
{
MidiEvent event=null;
try{
ShortMessage a=new ShortMessage();
a.setMessage(comd,chan,one,two);
event=new MidiEvent(a,tick);
}
catch(Exception e){e.printStackTrace();}
return event;
}


 
public void buildTrackAndStart()
{
int [] trackSheet=null;
sequence.deleteTrack(track);
track=sequence.createTrack();

for(int i=0;i<16;i++)
{
trackSheet=new int[16];
int key=instruments[i];

for(int j=0;j<16;j++){
JCheckBox jc=(JCheckBox)checkboxList.get(j+(16*i));
if(jc.isSelected()){
trackSheet[j]=key;
}
else trackSheet[j]=0;
}

makeTracks(trackSheet);
track.add(makeEvent(176,1,127,0,16));
}

track.add(makeEvent(192,9,1,0,15));
try{
sequencer.setSequence(sequence);
sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
sequencer.start();
sequencer.setTempoInBPM(120);
} catch(Exception e){e.printStackTrace();}
}

public void makeTracks(int[]list){
for(int i=0;i<16;i++){
int key=list[i];
if(key!=0){
track.add(makeEvent(144,9,key,100,i));
track.add(makeEvent(128,9,key,100,i+1));
}
}
}

public class MyStartListener implements ActionListener{
public void actionPerformed(ActionEvent a){
buildTrackAndStart();
}
}

public class MyStopListener implements ActionListener{
public void actionPerformed(ActionEvent a){
sequencer.stop();
}
}
public class MyUpTempoListener implements ActionListener{
public void actionPerformed(ActionEvent a){
float tempoFactor=sequencer.getTempoFactor();
sequencer.setTempoFactor((float)(tempoFactor*1.03));
}
}
public class MyDownTempoListener implements ActionListener{
public void actionPerformed(ActionEvent a){
float tempoFactor=sequencer.getTempoFactor();
sequencer.setTempoFactor((float)(tempoFactor*0.97));
}
}
}