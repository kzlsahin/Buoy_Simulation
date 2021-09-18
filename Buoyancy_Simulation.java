import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Buoyancy_Simulation extends PApplet {

float f, f_damped, damping_const, density, t, g,section_area, B, y_initial , sayac;
float mass_buoy, height_buoy, t_initial;
boolean refresh_loop=false;
float draft, acc, dt, velocity, draft_disp;
int scale;
float wave_period, wave_length, wave_amplitude, dx, theta, tCharge;
float [] wave_y;//piksel
boolean wave_var=false;
boolean overButton, overButtonAmp, overButtonWaveLength, buttonWaveLengthPressed;
boolean buttonAmpPressed=false;
String waveStatus, amp, not, not2, waveLength;
String amplitude= "";
String waveLengthString="";
int buoy_count, button1x, button1y, button1w, button1h, waveStartPeriod, clickCount = 0, button2x, button2y, button2w, button2h, button3x, button3y, button3w, button3h;
float acc2, draft2, velocity2,  mass_buoy2, height_buoy2, section_area2;
  float[] buoy_y= {height_buoy, height_buoy2};
  float[] buoy_x= {500, 250};
  float[] buoy_w= {0.5f, 0.5f};
public void setup(){ 
  frameRate(25);
  dt=40;
  
  background(50);
  textSize(30);
  density=1000;//kg/m3
  g=9.81f;//m/s2
  mass_buoy=200;//kg
  section_area=0.4f;//m2
  height_buoy=1.f;//m
  B=section_area*density*g;
  damping_const=0.6f;
  t_initial=0;
  y_initial=height_buoy;
  velocity=0;
  sayac=0;
  scale=80;
  draft_disp=1.f;
  //f_damped=sqrt(sq(f*2*PI)-sq(damping_const)/4)/(2*PI);
    //f=sqrt(B/mass_buoy)/(2*PI);//frequency
    
    
  //wave values
  wave_period=3;//sec.
  wave_length=6;
  wave_amplitude=0.8f;//should be modifiable
  theta=0;
  dx=(wave_length/wave_period)*(dt/1000)*scale;
  wave_y=new float[ceil(width/dx)];
  waveStatus="  ";
  for (int i = 0; i < wave_y.length; i++) {
    wave_y[i]=0;
  }
  
  
  //ButtonSinus
  button1x=80;
  button1y=80;
  button1w=200;
  button1h=-40;
  waveStartPeriod=100;
  tCharge=0;
  
  //ButonAmplitude
  button2x=500;
  button2y=80;
  button2w=450;
  button2h=-40;
  amp="  ";
  //ButtonWaveLength
  button3x=500;
  button3y=140;
  button3w=450;
  button3h=-40;
  waveLength="  ";
  
  //Buoy 2
  buoy_count=2;
  velocity2=0;
  mass_buoy2=200;//kg
  section_area2=0.4f;//m2
  height_buoy2=1;//m
  B=section_area2*density*g;
not="not:"+" ";
not2=" ";
}
public void draw(){
  update();
  background(50);

  if(wave_var){
    waveStatus="dalgayı kes";}
  else{
    waveStatus="sinus dalgasi";
  }  
  
  //ButtonSinus
  fill(155,155,0, 200);
  stroke(255);
  strokeWeight(4);
  rect(button1x, button1y, button1w, button1h);
  textSize(abs(button1h)/2);
  textAlign(CENTER);
  fill(0);
  text(waveStatus,button1x+button1w/2, button1y+button1h/4);
  if(wave_var){
    
  //ButtonAmp
  fill(155,155,0, 200);
  stroke(255);
  strokeWeight(4);
  rect(button2x, button2y, button2w, button2h);
  textSize(abs(button2h)/2);
  textAlign(CENTER);
  fill(0);
  if(buttonAmpPressed){amp="Please insert amplitude btw 0.2 and 2 then press enter";}
  else{amp="press to change amplitude of the wave";}
  text(amp,button2x+button2w/2, button2y+button2h/4);  
  
   //ButtonWaveLength
  fill(155,155,0, 200);
  stroke(255);
  strokeWeight(4);
  rect(button3x, button3y, button3w, button3h);
  textSize(abs(button3h)/2);
  textAlign(CENTER);
  fill(0);
  if(buttonWaveLengthPressed){waveLength="Please insert wave length in meter then press enter";}
  else{waveLength="press to change length of the wave";}
  text(waveLength,button3x+button3w/2, button3y+button3h/4);}
  
  pushMatrix();
  translate(0,500);
    calculateWave();
    renderWave();
    
    stroke(255,120,10);
    strokeWeight(4);
    fill(255,120,10);
    text(not,button3x,button3y-(4*scale));
    text("input string length:"+not2,button3x,button3y-(3.5f*scale));
    text("wave length"+wave_length,200,250);
    text("wave_amplitude:"+wave_amplitude,200,300);
    //line(0,0,width,0);
    draft=buoy_y[0]-wave_y[ceil((buoy_x[0]+buoy_w[0]/2)/dx)]/scale;
    draft2=buoy_y[1]-wave_y[ceil((buoy_x[1]+buoy_w[1]/2)/dx)]/scale;
    for(int i=0;i<buoy_count;i++){
    rect(buoy_x[i],scale*buoy_y[i], buoy_w[i]*scale, -scale*height_buoy);}
   
  popMatrix();   
     
  if(!refresh_loop&abs(draft_disp)>(1/scale)){
    draft_disp=draft;
     acc=(mass_buoy*g-B*draft-mass_buoy*damping_const*velocity)/mass_buoy;
     buoy_y[0]=buoy_y[0]+velocity*dt/1000+acc*sq(dt/1000)/2;
     draft_disp-=buoy_y[0];
     velocity=velocity+acc*dt/1000;
     
     acc2=(mass_buoy2*g-B*draft2-mass_buoy2*damping_const*velocity2)/mass_buoy2;
     buoy_y[1]=buoy_y[1]+velocity2*dt/1000+acc2*sq(dt/1000)/2;
     velocity2=velocity2+acc2*dt/1000;
     
    sayac+=dt;
  }
  theta+=2*PI*(dt/1000)/wave_period;
  if(wave_var&&tCharge<waveStartPeriod){
    tCharge+=1;}
   if(!wave_var&&tCharge>0){
     tCharge-=1;}
}
public void calculateWave(){
  if(wave_var){
  float angle=theta;
  for (int i = 0; i < wave_y.length; i++) {
    wave_y[i] = sin(angle)*wave_amplitude*scale*(tCharge/waveStartPeriod);
    angle+=2*PI*(dt/1000)/wave_period;
   }
  }
  if(!wave_var||tCharge>0) {
     float angle=theta;
    for (int i = 0; i < wave_y.length; i++) {
          wave_y[i] = sin(angle)*wave_amplitude*scale*(tCharge/waveStartPeriod)*(tCharge/waveStartPeriod);
          angle+=2*PI*(dt/1000)/wave_period;
   
   }
  }
}
public void renderWave() {
  noStroke();
  fill(0,100,255);
  // A simple way to draw the wave with an ellipse at each location
  /*for (int i = 0; i < wave_y.length; i++) {
    ellipse(i*dx, wave_y[i], scale/20, scale/20);
  }*/
  noFill();
  stroke(0, 0, 255);
  strokeWeight(4);
  beginShape();
  for (int i = 0; i < wave_y.length; i+=2) {
       curveVertex(i*dx, wave_y[i]);
  }
  endShape();
}
public void update(){
  if(overButtonSinus(button1x, button1y, button1w, button1h)){
      overButton=true;}
      else{ overButton=false;}
   if(wave_var && overButtonAmplitude(button2x, button2y, button2w, button2h)){
      overButtonAmp=true;}
      else{ overButtonAmp=false;}
      
      if(wave_var && overButtonWaveLength(button3x, button3y, button3w, button3h)){
      overButtonWaveLength=true;}
      else{ overButtonWaveLength=false;}
}
public void mouseClicked(){
  if(overButton){
    if(wave_var){
    wave_var=false;
    tCharge=waveStartPeriod; draft_disp=1.f;}
    else{wave_var=true; tCharge=1;draft_disp=1.f;}
  }  
    if(overButtonAmp&&!buttonAmpPressed){ buttonAmpPressed=true;}
    if(overButtonWaveLength&&!buttonWaveLengthPressed){ buttonWaveLengthPressed=true;}
  
  
  if(!refresh_loop && !overButton && !overButtonAmp&&!buttonAmpPressed &&!overButtonWaveLength){
    refresh_loop=true;
    sayac=0;
    for(int i =0;i<buoy_count;i++){
    buoy_y[i]=y_initial;}
    draft=y_initial;
    draft2=y_initial;
    draft_disp=1.f;
    velocity=0;
    velocity2=0;
    wave_amplitude=0.5f;
  }
  else{ refresh_loop=false;
}
}
public boolean overButtonSinus(int x, int y, int w, int h){
  if (mouseX >= x && mouseX <= x+w && 
      mouseY <= y && mouseY >= y+h) {
    return true;
  } else {
    return false;
  }
}

public boolean overButtonAmplitude(int x, int y, int w, int h){
  if (mouseX >= x && mouseX <= x+w && 
      mouseY <= y && mouseY >= y+h) {
    return true;
  } else {
    return false;
  }
}
public boolean overButtonWaveLength(int x, int y, int w, int h){
  if (mouseX >= x && mouseX <= x+w && 
      mouseY <= y && mouseY >= y+h) {
    return true;
  } else {
    return false;
  }
}

public void keyPressed(){
   not2=str(amplitude.length());
   if (key==ENTER && buttonAmpPressed&&not!="unaccepted character"){
    wave_amplitude=PApplet.parseFloat(amplitude);
    not="amplitude of the wave has changed to "+wave_amplitude;
    buttonAmpPressed=false;
    amplitude="";
    not2=amplitude;
}
else if(key==ENTER && buttonAmpPressed&&not=="unaccepted character"){
  not="wave amplitude is same  "+wave_amplitude;
    buttonAmpPressed=false;
    amplitude="";
  not2=amplitude;}
    
    
 if(buttonAmpPressed&&key!=ENTER){
   if(amplitude.length()==0&&key>='0'&&key<'2'){
   amplitude+=key;
   not="key pressed:"+key;
    not2=amplitude;
 }
 else if(amplitude.length()==2&&key>='0'&&key<='9'){
   amplitude+=key;
   not="key pressed:"+key;
  not2=amplitude;
 }
  else if(amplitude.length()==1&&key=='.' ){
   amplitude+=key;
     not="key pressed:"+key;
   not2=amplitude;
}
 else{not="unaccepted character";}

 }
 
 if(buttonWaveLengthPressed&&key!=ENTER){
  if(waveLengthString.length()==0&&key>='0'&&key<='9'){
   waveLengthString+=key;
   not="key pressed:"+key;
   not2=waveLengthString;
 }
 else if(waveLengthString.length()==2&&key>='0'&&key<'9'){
   waveLengthString+=key;
   not="key pressed:"+key;
  not2=waveLengthString;
 }
  else if(waveLengthString.length()==1&&key=='.' ){
   waveLengthString+=key;
     not="key pressed:"+key;
   not2=waveLengthString;
} 
 else{not="unaccepted character"; }

 }
 if (key==ENTER && buttonWaveLengthPressed&&not!="unaccepted character"){
   wave_length=PApplet.parseFloat(waveLengthString);
   dx=(wave_length/wave_period)*(dt/1000)*scale;
   waveLengthString="";
   buttonWaveLengthPressed=false;
   //refresh_loop=true;
    sayac=0;
    for(int i =0;i<buoy_count;i++){
    buoy_y[i]=y_initial;}
    draft=y_initial;
    draft2=y_initial;
    draft_disp=1.f;
    velocity=0;
    velocity2=0;
    theta=0;
    wave_y=new float[ceil(width/dx)];
    not="length of the wave has changed to "+wave_length;
    not2=waveLengthString;
 }
 else if(key==ENTER && buttonWaveLengthPressed&&not=="unaccepted character"){
   not="length of wave is same  "+wave_length;
    buttonWaveLengthPressed=false;
    waveLengthString="";
  not2=waveLengthString;}
}
  public void settings() {  size(1200,900); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Buoyancy_Simulation" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
