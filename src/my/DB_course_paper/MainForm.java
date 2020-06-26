/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.DB_course_paper;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import javax.swing.table.*;
/**
 *
 * @author Matt
 */
public class MainForm extends javax.swing.JFrame {
    private Connection connection = null;
    private TableTemplates tables=new TableTemplates();
    private String login;
    private String password;
    private String insert;
    /**
     * Creates new form MainForm
     */
    
    public MainForm() {
        LogForm log=new LogForm(this,true);
        log.setVisible(true);
        login=log.login;
        password=log.password;
        if(log.cont){ 
            initConnection();
            initComponents();
            this.jPanel10.setVisible(false);
            this.jPanel17.setVisible(false);
        }
        else System.exit(0);
    
    }
    private void initConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/db_course_paper?useUnicode=true&serverTimezone=Asia/Yekaterinburg", login, password);
            }
            catch(Exception ex){
             String problem="<html>Ошибка подключения!<br>Проблема: "+ex.getMessage()+"<html>";
             ReportForm rep=new ReportForm(this,true,problem);
             rep.setVisible(true);
            }
    }
////////////////////////////////////////////////////////////////////////////////
    /*Блок функций */
////////////////////////////////////////////////////////////////////////////////
    private ArrayList<Object[]> make_call_select(String SQL,String[] type_parameters,Object[] parameters,int num_of_cols){
        ArrayList<Object[]> res=new ArrayList();
        try{
            CallableStatement call=connection.prepareCall(SQL);
            for(int i=0;i<parameters.length;i++){
                if(type_parameters[i]=="String"){
                    call.setString(i+1, (String)parameters[i]);
                }
                if(type_parameters[i]=="Int"){
                     call.setInt(i+1, (int)parameters[i]);
                }
                if(type_parameters[i]=="Date"){
                     call.setDate(i+1, (Date)parameters[i]);
                }
            }
            ResultSet result=call.executeQuery();
            while(result.next()){
                Object[] res_row=new Object[num_of_cols];
                for(int j=0;j<num_of_cols;j++){
                    res_row[j]=result.getObject(j+1);
                    if(res_row[j]!=null){
                    if (res_row[j].toString().matches("....-..-..")){
                        res_row[j]=show_date(res_row[j].toString());
                    }
                    }
                }
                res.add(res_row);
            }
            result.close();
            call.close();
           }
        
        catch(Exception ex){
             String problem="<html>Ошибка выполнения запроса!<br>Проблема: "+ex.getMessage()+"<html>";
             ReportForm rep=new ReportForm(this,true,problem);
             rep.setVisible(true);
        }
        return res;
    }
    private void make_call_change(String SQL,String[] type_parameters,Object[] parameters){
        try{
            CallableStatement call=connection.prepareCall(SQL);
            for(int i=0;i<parameters.length;i++){
                if(type_parameters[i]=="String"){
                    call.setString(i+1, (String)parameters[i]);
                }
                if(type_parameters[i]=="Bool"){
                    call.setBoolean(i+1, (Boolean)parameters[i]);
                }
                if(type_parameters[i]=="Int"){
                     call.setInt(i+1, (int)parameters[i]);
                }
                if(type_parameters[i]=="Date"){
                     call.setDate(i+1, (Date)parameters[i]);
                }
            }
            call.executeUpdate();
        }
        catch(Exception ex){
             String problem="<html>Ошибка выполнения запроса!<br>Проблема: "+ex.getMessage()+"<html>";
             ReportForm rep=new ReportForm(this,true,problem);
             rep.setVisible(true);
        }
    }
    private DefaultTableModel fill_table(ArrayList<Object[]> data,int num_of_table){
        DefaultTableModel table=tables.getTableModel(num_of_table);
        table.setRowCount(0);
        for(int i=0;i<data.size();i++){
            table.insertRow(table.getRowCount(), data.get(i));
        }
        return table;
    }
    private Object[] get_table_data(TableModel table,int selected){
        int cols=table.getColumnCount();
        Object[] res=new Object[cols];
        for(int i=0;i<cols;i++){
            res[i]=table.getValueAt(selected, i);
        }
        return res;
    }
    private String show_date(Object date){
        String[] mas=date.toString().split("-");
        return mas[2]+"-"+mas[1]+"-"+mas[0];
    }
    private Date make_date(String date){
        String[] mas=date.split("-");
        GregorianCalendar temp=new GregorianCalendar(Integer.parseInt(mas[2]),Integer.parseInt(mas[1]),Integer.parseInt(mas[0]));
        return new Date(temp.getTime().getTime());
        
    }
    private void Update_tables(){

        int tab=jTabbedPane1.getSelectedIndex();
        Clear_textboxes(tab);
        try{
        if(tab==0){
            String SQL="{CALL Search_employee(?,?,?)}";
            Object[] parameters={"","",""};
            String[] types={"String","String","String"};
            this.Employee.setModel(fill_table(make_call_select(SQL,types,parameters,11),0));
        }
        if(tab==1){
            String SQL="{CALL Search_engineer(?,?,?,?,?,?)}";
            Object[] parameters={"","","","","",""};
            String[] types={"String","String","String","String","String","String"};
            this.Jobs.setModel(fill_table(make_call_select(SQL,types,parameters,10),1));
            this.First_2.setSelected(true);
            job_switch(1);
        }
        if(tab==2){
            String SQL="{CALL Search_manager(?,?,?,?,?,?,?)}";
            Object[] parameters={"","","","","","",""};
            String[] types={"String","String","String","String","String","String","String"};
            this.Managers.setModel(fill_table(make_call_select(SQL,types,parameters,9),7));
            this.jRadioButton23.setSelected(true);
        }
        if(tab==3){
            String SQL="{CALL Search_department(?,?,?,?)}";
            Object[] parameters={"","","",""};
            String[] types={"String","String","String","String"};
            this.Department.setModel(fill_table(make_call_select(SQL,types,parameters,6),8));
            this.jButton8.setVisible(false);
           this.jButton9.setVisible(false);
           this.jPanel23.setVisible(false);
            
        }
        if(tab==4){
             String SQL="{CALL Search_contract(?,?,?,?,?)}";
            Object[] parameters={"","","","",""};
            String[] types={"String","String","String","String","String"};
            this.Contract.setModel(fill_table(make_call_select(SQL,types,parameters,11),9));
             jButton10.setVisible(true);
            jButton13.setVisible(false);
            jButton14.setVisible(false);
            jButton27.setVisible(false);
        }
        if(tab==5){
            String SQL="{CALL Search_project(?,?,?)}";
            Object[] parameters={"","",""};
            String[] types={"String","String","String"};
            this.Project.setModel(fill_table(make_call_select(SQL,types,parameters,12),10));
        }
        if(tab==6){
            String SQL="{CALL Search_equipment(?,?)}";
            Object[] parameters={"",""};
            String[] types={"String","String"};
            this.Equipment.setModel(fill_table(make_call_select(SQL,types,parameters,7),11));
            jButton19.setVisible(true);
            jButton20.setVisible(false);
            jButton21.setVisible(false);
        }
        if(tab==7){
            String SQL="{CALL Search_subcontractor(?,?,?)}";
            Object[] parameters={"","",""};
            String[] types={"String","String","String"};
            this.Subcontractors.setModel(fill_table(make_call_select(SQL,types,parameters,8),12));
        }
        if(tab==8){
            this.jRadioButton6.setSelected(true);
            String SQL="SELECT * FROM group_emp";
            ArrayList<Object[]> res=new ArrayList();
            PreparedStatement temp=connection.prepareStatement(SQL);
            ResultSet result=temp.executeQuery();
            while(result.next()){
                Object[] res_row=new Object[2];
                for(int j=0;j<2;j++){
                    res_row[j]=result.getObject(j+1);
                    if(res_row[j]!=null){
                    if (res_row[j].toString().matches("....-..-..")){
                        res_row[j]=show_date(res_row[j].toString());
                    }
                    }
                }
                res.add(res_row);
            }
            this.Groups.setModel(fill_table(res,13));
        }
        
        }
        
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    private void Clear_textboxes(int panel){
            if(panel==0){//подчистка текстбоксов
                jTextField1.setText("");
                jTextField2.setText("");
                jTextField3.setText("");
                jTextField4.setText("");
                jTextField5.setText("");
                jTextField6.setText("");
                jTextField7.setText("");
                jTextField8.setText("");
                jTextField10.setText("");
                jTextField11.setText("");
                jTextField12.setText("");
                jComboBox2.setSelectedIndex(0);
                jButton2.setVisible(true);
                jPanel17.setVisible(false);
            }
            if (panel==3){
                jTextField41.setText("");
                jTextField42.setText("");
                jTextField43.setText("");
                jTextField13.setText(""); 
            }
            if (panel==4){
                jTextField44.setText("");
                jTextField45.setText("");
                jTextField46.setText("");
                jTextField57.setText(""); 
                jTextField58.setText(""); 
                jTextField59.setText(""); 
            }
            if (panel==5){
                jTextField47.setText("");
                jTextField48.setText("");
                jTextField49.setText("");
                
            }
            if (panel==7){
                jTextField14.setText("");
                jTextField15.setText("");
                jTextField16.setText("");
                jTextField17.setText(""); 
            }
        }
    private void switch_insert(int num_of_button){
        if(num_of_button==1){
            jLabel10.setText("Категория");
            jLabel11.setText("Научная степень");
            jLabel12.setText("Специализация");
            jLabel11.setVisible(true);
            jLabel12.setVisible(true);
            jTextField11.setVisible(true);
            jTextField12.setVisible(true);
        }
        if(num_of_button==2){
            jLabel10.setText("Число патентных свидетельств");
            jLabel11.setText("Научная степень");
            jLabel12.setText("Сфера разработок");
            jLabel11.setVisible(true);
            jLabel12.setVisible(true);
            jTextField11.setVisible(true);
            jTextField12.setVisible(true);
        }
        if(num_of_button==3){
            jLabel10.setText("Число исследований");
            jLabel11.setText("Сфера исследований");
            jLabel12.setText("Число публикаций");
            jLabel11.setVisible(true);
            jLabel12.setVisible(true);
            jTextField11.setVisible(true);
            jTextField12.setVisible(true);
        }
        if(num_of_button==4){
            jLabel10.setText("Количество обслуживаемого оборуд.");
            jLabel11.setText("Специализация");
            jLabel11.setVisible(true);
            jLabel12.setVisible(false);
            jTextField11.setVisible(true);
            jTextField12.setVisible(false);
        }
        if(num_of_button==5){
            jLabel10.setText("Вид обслуживания");
            jLabel11.setVisible(false);
            jLabel12.setVisible(false);
            jTextField11.setVisible(false);
            jTextField12.setVisible(false);
        }
    }
    private void job_switch(int num_of_button){
        if(Jobs.getSelectedRow()!=-1) this.jButton15.setVisible(true);
        else this.jButton15.setVisible(false);
        jTextField19.setText("");
        jTextField20.setText("");
        jTextField21.setText("");
        jTextField22.setText("");
        jTextField23.setText("");
        jTextField24.setText("");
        if(num_of_button==1){
            jLabel19.setText("Категория");
            jLabel20.setText("Научная степень");
            jLabel21.setText("Специализация");
            jLabel19.setVisible(true);
            jLabel20.setVisible(true);
            jLabel21.setVisible(true);
            jTextField19.setVisible(true);
            jTextField20.setVisible(true);
            jTextField21.setVisible(true);
            
        }
        if(num_of_button==2){
            jLabel19.setText("Число патентных свидетельств");
            jLabel20.setText("Научная степень");
            jLabel21.setText("Сфера разработок");
            jLabel19.setVisible(true);
            jLabel20.setVisible(true);
            jLabel21.setVisible(true);
            jTextField19.setVisible(true);
            jTextField20.setVisible(true);
            jTextField21.setVisible(true);
      
        }
        if(num_of_button==3){
            jLabel19.setText("Число исследований");
            jLabel20.setText("Сфера исследований");
            jLabel21.setText("Число публикаций");
            jLabel19.setVisible(true);
            jLabel20.setVisible(true);
            jLabel21.setVisible(true);
            jTextField19.setVisible(true);
            jTextField20.setVisible(true);
            jTextField21.setVisible(true);

        }
        if(num_of_button==4){
            jLabel19.setText("Количество обслуживаемого оборуд.");
            jLabel20.setText("Специализация");
            jLabel19.setVisible(true);
            jLabel20.setVisible(true);
            jLabel21.setVisible(false);
            jTextField19.setVisible(true);
            jTextField20.setVisible(true);
            jTextField21.setVisible(false);

        }
        if(num_of_button==5){
            jLabel19.setText("Вид обслуживания");
            jLabel19.setVisible(true);
            jLabel20.setVisible(false);
            jLabel21.setVisible(false);
            jTextField19.setVisible(true);
            jTextField20.setVisible(false);
            jTextField21.setVisible(false);
 
        }
        if(num_of_button==6){
            this.jButton15.setVisible(false);
            jLabel19.setVisible(false);
            jLabel20.setVisible(false);
            jLabel21.setVisible(false);
            jTextField19.setVisible(false);
            jTextField20.setVisible(false);
            jTextField21.setVisible(false);

        }
    }
private int last_ins_id(){
    int id=0;
    String SQL="SELECT last_insert_id()";
    try{
        PreparedStatement temp=connection.prepareStatement(SQL);
        ResultSet res=temp.executeQuery();
        res.next();
        id=res.getInt(1);
    }
    catch(Exception ex){
             String problem="<html>Ошибка выполнения запроса!<br>Проблема: "+ex.getMessage()+"<html>";
             ReportForm rep=new ReportForm(this,true,problem);
             rep.setVisible(true);
        }
    return id;
}
private int get_job_id(int ide){
    int id=0;
    String SQL="SELECT id_job FROM job WHERE id_employee=?";
    try{
    PreparedStatement temp=connection.prepareStatement(SQL);
    temp.setInt(1, ide);
    ResultSet res=temp.executeQuery();
    res.next();
    id=res.getInt(1);
    }
    catch(Exception ex){
             String problem="<html>Ошибка выполнения запроса!<br>Проблема: "+ex.getMessage()+"<html>";
             ReportForm rep=new ReportForm(this,true,problem);
             rep.setVisible(true);
        }
    return id;
}
private int get_eng_id(int ide){
    int id=0;
    String SQL="SELECT id_engineer FROM engineer WHERE id_job=?";
    try{
    PreparedStatement temp=connection.prepareStatement(SQL);
    temp.setInt(1, ide);
    ResultSet res=temp.executeQuery();
    res.next();
    id=res.getInt(1);
    }
    catch(Exception ex){
             String problem="<html>Ошибка выполнения запроса!<br>Проблема: "+ex.getMessage()+"<html>";
             ReportForm rep=new ReportForm(this,true,problem);
             rep.setVisible(true);
        }
    return id;
}
private int get_con_id(int ide){
    int id=0;
    String SQL="SELECT id_constructor FROM constructor WHERE id_job=?";
    try{
    PreparedStatement temp=connection.prepareStatement(SQL);
    temp.setInt(1, ide);
    ResultSet res=temp.executeQuery();
    res.next();
    id=res.getInt(1);
    }
    catch(Exception ex){
             String problem="<html>Ошибка выполнения запроса!<br>Проблема: "+ex.getMessage()+"<html>";
             ReportForm rep=new ReportForm(this,true,problem);
             rep.setVisible(true);
        }
    return id;
}
private boolean en_or_con(int ide){
    String result="";
    String SQL="SELECT job_name FROM job WHERE id_employee=?";
    try{
    PreparedStatement temp=connection.prepareStatement(SQL);
    temp.setInt(1, ide);
    ResultSet res=temp.executeQuery();
    res.next();
    result=res.getString(1);
    }
    catch(Exception ex){
             String problem="<html>Ошибка выполнения запроса!<br>Проблема: "+ex.getMessage()+"<html>";
             ReportForm rep=new ReportForm(this,true,problem);
             rep.setVisible(true);
        }
    return result.equals("Инженер");
}
private int get_customer_id(int idc){
    int id=0;
    String SQL="SELECT id_customer FROM contract WHERE id_contract=?";
    try{
    PreparedStatement temp=connection.prepareStatement(SQL);
    temp.setInt(1, idc);
    ResultSet res=temp.executeQuery();
    res.next();
    id=res.getInt(1);
    }
    catch(Exception ex){
             String problem="<html>Ошибка выполнения запроса!<br>Проблема: "+ex.getMessage()+"<html>";
             ReportForm rep=new ReportForm(this,true,problem);
             rep.setVisible(true);
        }
    return id;
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    ////////////////////////////////////////////////////////////////////////////////
    /*Блок обработчиков */
////////////////////////////////////////////////////////////////////////////////
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        Employee = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox<>();
        jPanel17 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jTextField10 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jButton6 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jTextField8 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Jobs = new javax.swing.JTable();
        jPanel13 = new javax.swing.JPanel();
        jTextField19 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jTextField20 = new javax.swing.JTextField();
        jTextField21 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        First_2 = new javax.swing.JRadioButton();
        jRadioButton17 = new javax.swing.JRadioButton();
        jRadioButton18 = new javax.swing.JRadioButton();
        jRadioButton19 = new javax.swing.JRadioButton();
        jRadioButton20 = new javax.swing.JRadioButton();
        jButton15 = new javax.swing.JButton();
        jRadioButton24 = new javax.swing.JRadioButton();
        jPanel14 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jTextField22 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jTextField23 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jTextField24 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Managers = new javax.swing.JTable();
        jPanel18 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jTextField34 = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jTextField35 = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jTextField36 = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jTextField37 = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jTextField39 = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jRadioButton21 = new javax.swing.JRadioButton();
        jRadioButton22 = new javax.swing.JRadioButton();
        jLabel47 = new javax.swing.JLabel();
        jTextField40 = new javax.swing.JTextField();
        jRadioButton23 = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        Department = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jTextField41 = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        jTextField42 = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jTextField43 = new javax.swing.JTextField();
        jPanel23 = new javax.swing.JPanel();
        jButton23 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        Contract = new javax.swing.JTable();
        jPanel21 = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        jTextField44 = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        jTextField45 = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        jTextField46 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jPanel25 = new javax.swing.JPanel();
        jLabel67 = new javax.swing.JLabel();
        jTextField57 = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        jTextField58 = new javax.swing.JTextField();
        jLabel69 = new javax.swing.JLabel();
        jTextField59 = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        jButton25 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        Project = new javax.swing.JTable();
        jPanel22 = new javax.swing.JPanel();
        jLabel55 = new javax.swing.JLabel();
        jTextField47 = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        jTextField48 = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        jTextField49 = new javax.swing.JTextField();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jPanel26 = new javax.swing.JPanel();
        jLabel71 = new javax.swing.JLabel();
        jTextField60 = new javax.swing.JTextField();
        jLabel72 = new javax.swing.JLabel();
        jTextField61 = new javax.swing.JTextField();
        jLabel73 = new javax.swing.JLabel();
        jTextField62 = new javax.swing.JTextField();
        jLabel74 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        Equipment = new javax.swing.JTable();
        jPanel12 = new javax.swing.JPanel();
        jTextField14 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jTextField16 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        Subcontractors = new javax.swing.JTable();
        jPanel15 = new javax.swing.JPanel();
        jTextField18 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jTextField25 = new javax.swing.JTextField();
        jTextField26 = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jTextField27 = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jButton24 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        jTextField28 = new javax.swing.JTextField();
        jTextField29 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        Groups = new javax.swing.JTable();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jScrollPane11 = new javax.swing.JScrollPane();
        GroupContent = new javax.swing.JTable();
        jButton12 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel30 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Система");

        jTabbedPane1.setName(""); // NOI18N
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });
        jTabbedPane1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                none(evt);
            }
        });

        Employee.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Имя", "Фамилия", "Отчество", "Пол", "Дата рождения", "Зарплата", "График работы", "Образование", "Отдел", "Группа"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Employee.setName(""); // NOI18N
        Employee.getTableHeader().setReorderingAllowed(false);
        Employee.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                EmployeeMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(Employee);

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });

        jLabel1.setText("Имя");

        jLabel2.setText("Фамилия");

        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField2KeyTyped(evt);
            }
        });

        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField3KeyTyped(evt);
            }
        });

        jLabel3.setText("Отчество");

        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField4KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField4KeyTyped(evt);
            }
        });

        jLabel4.setText("Пол");

        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField5KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField5KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField5KeyTyped(evt);
            }
        });

        jLabel5.setText("Дата рождения");

        jTextField6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                Anti_letters(evt);
            }
        });

        jLabel6.setText("Зарплата");

        jLabel7.setText("Режим работы");

        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField7KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField7KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField7KeyTyped(evt);
            }
        });

        jLabel8.setText("Образование");

        jButton2.setText("Нанять сотрудника");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Начальное", "Среднее общее", "Среднее профессиональное", "Высшее" }));

        jButton3.setText("Уволить сотрудника");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Обновить данные сотрудника");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField1)
            .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextField3)
            .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jTextField5, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTextField4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel10.setText("Атрибут 1");

        jLabel11.setText("Атрибут 2");

        jLabel12.setText("Атрибут 3");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Инженер");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Конструктор");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("Лаборант");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setText("Техник");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton5);
        jRadioButton5.setText("Обслуживающий персонал");
        jRadioButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Отменить ");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton5.setText("Продолжить");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jTextField8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                Anti_letters(evt);
            }
        });

        jLabel9.setText("Стаж работы");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField10)
            .addComponent(jTextField11)
            .addComponent(jTextField12)
            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton3)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton4)
                    .addComponent(jRadioButton5)
                    .addComponent(jLabel9)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel9)
                .addGap(3, 3, 3)
                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton3)
                .addGap(3, 3, 3)
                .addComponent(jRadioButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton6)
                .addGap(143, 143, 143))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(184, 184, 184))))
        );

        jTabbedPane1.addTab("Сотрудники", jPanel1);

        Jobs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "oaos", "asfafs", "asf", "asf", "afs", "afs", "asf", "asf", "agsasf", "Title 10"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Jobs.getTableHeader().setReorderingAllowed(false);
        Jobs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JobsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Jobs);

        jTextField19.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField19KeyReleased(evt);
            }
        });

        jLabel19.setText("Атрибут 1");

        jLabel20.setText("Атрибут 2");

        jTextField20.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField19KeyReleased(evt);
            }
        });

        jTextField21.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField19KeyReleased(evt);
            }
        });

        jLabel21.setText("Атрибут 3");

        buttonGroup2.add(First_2);
        First_2.setText("Инженер");
        First_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                First_2ActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioButton17);
        jRadioButton17.setText("Конструктор");
        jRadioButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton17ActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioButton18);
        jRadioButton18.setText("Лаборант");
        jRadioButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton18ActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioButton19);
        jRadioButton19.setText("Техник");
        jRadioButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton19ActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioButton20);
        jRadioButton20.setText("Обслуживающий персонал");
        jRadioButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton20ActionPerformed(evt);
            }
        });

        jButton15.setText("Обновить данные");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        buttonGroup2.add(jRadioButton24);
        jRadioButton24.setText("Начальник отдела");
        jRadioButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton24ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addComponent(jLabel20)
                    .addComponent(jLabel19)
                    .addComponent(First_2)
                    .addComponent(jRadioButton18)
                    .addComponent(jRadioButton17)
                    .addComponent(jRadioButton19)
                    .addComponent(jRadioButton20)
                    .addComponent(jRadioButton24))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextField21)
            .addComponent(jTextField20)
            .addComponent(jTextField19)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(First_2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton18)
                .addGap(3, 3, 3)
                .addComponent(jRadioButton19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jButton15)
                .addContainerGap())
        );

        jLabel22.setText("Имя");

        jTextField22.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField19KeyReleased(evt);
            }
        });

        jLabel23.setText("Фамилия");

        jTextField23.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField19KeyReleased(evt);
            }
        });

        jLabel24.setText("Отчество");

        jTextField24.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField19KeyReleased(evt);
            }
        });

        jLabel25.setText("Поиск сотрудника по ФИО");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField22, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jTextField23)
            .addComponent(jTextField24)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(jLabel23)
                    .addComponent(jLabel22)
                    .addComponent(jLabel25))
                .addGap(0, 46, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(263, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Должности", jPanel2);

        Managers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID ", "Имя", "Фамилия", "Отчетство", "Должность", "Стаж работы", "Образование", "Научная степень", "Структура"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Managers.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(Managers);

        jLabel38.setText("Имя");

        jTextField34.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField34KeyReleased(evt);
            }
        });

        jLabel39.setText("Фамилия");

        jTextField35.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField34KeyReleased(evt);
            }
        });

        jLabel40.setText("Отчество");

        jTextField36.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField34KeyReleased(evt);
            }
        });

        jLabel41.setText("Поиск сотрудника по ФИО");

        jLabel42.setText("Образование");

        jTextField37.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField34KeyReleased(evt);
            }
        });

        jLabel43.setText("Личные качества");

        jLabel45.setText("Научная степень");

        jTextField39.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField34KeyReleased(evt);
            }
        });

        jLabel46.setText("Должность");

        buttonGroup3.add(jRadioButton21);
        jRadioButton21.setText("Инженер");
        jRadioButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton23ActionPerformed(evt);
            }
        });

        buttonGroup3.add(jRadioButton22);
        jRadioButton22.setText("Конструктор");
        jRadioButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton23ActionPerformed(evt);
            }
        });

        jLabel47.setText("Управляемая структура");

        jTextField40.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField34KeyReleased(evt);
            }
        });

        buttonGroup3.add(jRadioButton23);
        jRadioButton23.setText("Все");
        jRadioButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton23ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField34, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jTextField35)
            .addComponent(jTextField36)
            .addComponent(jTextField37)
            .addComponent(jTextField39)
            .addComponent(jTextField40)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40)
                    .addComponent(jLabel39)
                    .addComponent(jLabel38)
                    .addComponent(jLabel41)
                    .addComponent(jLabel43)
                    .addComponent(jLabel42)
                    .addComponent(jLabel45)
                    .addComponent(jRadioButton21)
                    .addComponent(jRadioButton22)
                    .addComponent(jLabel46)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton23))
                .addGap(0, 45, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel39)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jLabel43)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel45)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel46)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton23)
                .addGap(7, 7, 7)
                .addComponent(jLabel47)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(77, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(262, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Руководители", jPanel3);

        Department.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID отдела", "Начальник", "Фамилия", "Отчество", "Количество сотрудников", "Направление деятельности"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Department.getTableHeader().setReorderingAllowed(false);
        Department.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DepartmentMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(Department);

        jLabel13.setText("Направление деятельности");

        jTextField13.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField13KeyReleased(evt);
            }
        });

        jButton9.setText("Обновить данные");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton8.setText("Удалить отдел");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton7.setText("Добавить отдел");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel48.setText("Имя");

        jTextField41.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField13KeyReleased(evt);
            }
        });

        jLabel49.setText("Фамилия");

        jTextField42.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField13KeyReleased(evt);
            }
        });

        jLabel51.setText("Поиск по ФИО начальника");

        jLabel50.setText("Отчество");

        jTextField43.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField13KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField41, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jTextField42)
            .addComponent(jTextField43)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel49)
                    .addComponent(jLabel48)
                    .addComponent(jLabel51)
                    .addComponent(jLabel50))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jLabel51)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel48)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel49)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel50)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jButton23.setText("Приписать оборудование к отделу");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jButton22.setText("Добавить сотрудника в отдел");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton29.setText("Просмотреть сотрудников отдела");
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        jButton11.setText("Просмотреть оборудование отдела");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jButton23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton22)
                .addGap(18, 18, 18)
                .addComponent(jButton29)
                .addGap(4, 4, 4)
                .addComponent(jButton11)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField13)
            .addComponent(jButton8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9)
                .addGap(18, 18, 18)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(201, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Отделы", jPanel4);

        Contract.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID договора", "Общая стоимость", "Суммарные затраты времени", "Дата заключения", "Заказчик", "Телефон заказчика", "Процент выполнения", "ID руководителя", "Имя", "Фамилия", "Отчество"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Contract.getTableHeader().setReorderingAllowed(false);
        Contract.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ContractMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(Contract);

        jLabel52.setText("Дата заключения");

        jLabel53.setText("Заказчик");

        jTextField45.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField45KeyReleased(evt);
            }
        });

        jLabel54.setText("Телефон заказчика");

        jTextField46.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField45KeyReleased(evt);
            }
        });

        jButton10.setText("Заключить договор");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton13.setText("Завершить договор");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText("Обновить данные");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField44, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jTextField45)
            .addComponent(jTextField46)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel54)
                    .addComponent(jLabel53)
                    .addComponent(jLabel52))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
            .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(jLabel52)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel53)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel54)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton14)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jLabel67.setText("Имя");

        jTextField57.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField45KeyReleased(evt);
            }
        });

        jLabel68.setText("Фамилия");

        jTextField58.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField45KeyReleased(evt);
            }
        });

        jLabel69.setText("Отчество");

        jTextField59.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField45KeyReleased(evt);
            }
        });

        jLabel70.setText("Поиск по ФИО руководителя");

        jButton25.setText("Обновить данные");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField57, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jTextField58)
            .addComponent(jTextField59)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel69)
                    .addComponent(jLabel68)
                    .addComponent(jLabel67)
                    .addComponent(jLabel70))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jButton25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addComponent(jLabel70)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel68)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel69)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(jButton25))
        );

        jButton27.setText("Показать проекты договора");
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(254, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jButton27)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Договоры", jPanel5);

        Project.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID проекта", "Срок сдачи", "Стоимость", "Объём работ", "Дата старта", "Дата выполнения", "Статус", "ID договора", "ID руководителя", "Имя", "Фамилия", "Отчество"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Project.getTableHeader().setReorderingAllowed(false);
        jScrollPane6.setViewportView(Project);

        jLabel55.setText("Срок сдачи");

        jLabel56.setText("Стоимость");

        jLabel57.setText("Объём работ (ч)");

        jButton16.setText("Добавить проект");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton17.setText("Завершить проект");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setText("Обновить данные");

        jButton30.setText("Просмотреть субподрядчиков");
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField47, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jTextField48)
            .addComponent(jTextField49)
            .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
            .addComponent(jButton17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel57)
                    .addComponent(jLabel56)
                    .addComponent(jLabel55))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jButton30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addComponent(jLabel55)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel56)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel57)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(jButton30)
                .addContainerGap())
        );

        jLabel71.setText("Имя");

        jLabel72.setText("Фамилия");

        jLabel73.setText("Отчество");

        jLabel74.setText("Поиск по ФИО руководителя");

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField60, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jTextField61)
            .addComponent(jTextField62)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel73)
                    .addComponent(jLabel72)
                    .addComponent(jLabel71)
                    .addComponent(jLabel74))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addComponent(jLabel74)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel71)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel72)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel73)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(247, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Проекты", jPanel6);

        Equipment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID оборудования", "Тип оборудования", "Производитель", "Срок работы", "Рабочая нагрузка", "ID группы", "ID отдела"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Equipment.getTableHeader().setReorderingAllowed(false);
        Equipment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                EquipmentMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(Equipment);

        jTextField14.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField14KeyReleased(evt);
            }
        });

        jLabel14.setText("Тип оборудования");

        jLabel15.setText("Производитель");

        jTextField15.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField14KeyReleased(evt);
            }
        });

        jLabel16.setText("Срок работы (ч)");

        jLabel17.setText("Нагрузка (ч/день)");

        jButton19.setText("Купить оборудование");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setText("Списать оборудование");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setText("Обновить данные");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField14)
            .addComponent(jTextField15, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jButton19, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
            .addComponent(jButton20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel15)
                    .addComponent(jLabel14)
                    .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jTextField17, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton21)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 262, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Оборудование", jPanel7);

        Subcontractors.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID субподрядчика", "Название организации", "Адрес", "Контактный телефон", "Стоимость найма", "Переданный объём работ", "Сроки сдачи", "ID проекта"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Subcontractors.getTableHeader().setReorderingAllowed(false);
        jScrollPane8.setViewportView(Subcontractors);

        jLabel18.setText("Организация");

        jLabel26.setText("Стоимость найма");

        jLabel27.setText("Переданный объём работ (ч)");

        jLabel28.setText("Срок сдачи");

        jButton24.setText("Нанять субподрядчика");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jButton26.setText("Обновить данные");

        jLabel29.setText("Контактный телефон");

        jLabel31.setText("Адрес организации");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField18)
            .addComponent(jButton24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jButton26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                        .addComponent(jLabel31)
                        .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel28)
                        .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField27)
                        .addComponent(jTextField26)
                        .addComponent(jLabel29)
                        .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel26))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26)
                .addGap(7, 7, 7)
                .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(jButton24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton26)
                .addContainerGap(112, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 262, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Субподрядчики", jPanel8);

        Groups.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID группы", "ID проекта"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Groups.getTableHeader().setReorderingAllowed(false);
        Groups.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                GroupsMouseClicked(evt);
            }
        });
        jScrollPane10.setViewportView(Groups);

        buttonGroup4.add(jRadioButton6);
        jRadioButton6.setText("Сотрудники");
        jRadioButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton6ActionPerformed(evt);
            }
        });

        buttonGroup4.add(jRadioButton7);
        jRadioButton7.setText("Оборудование");
        jRadioButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton7ActionPerformed(evt);
            }
        });

        GroupContent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        GroupContent.getTableHeader().setReorderingAllowed(false);
        jScrollPane11.setViewportView(GroupContent);

        jButton12.setText("Включить сотрудника в группу");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton28.setText("Выдать оборудование группе");
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 1199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButton6)
                    .addComponent(jRadioButton7)
                    .addComponent(jButton28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jRadioButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jRadioButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton28))
                    .addComponent(jScrollPane11))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Группы", jPanel20);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Приёма/увольнения сотрудников", "Приёма/списания оборудования", "Старта/выполнения проектов", "Заключения/выполнения договоров" }));

        jLabel30.setText("Журналы");

        jButton1.setText("Просмотреть");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 10, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabel30)
                .addGap(8, 8, 8)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
       switch_insert(2);
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton17ActionPerformed
        job_switch(2);
        Update_jobs();
    }//GEN-LAST:event_jRadioButton17ActionPerformed

    private void none(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_none
        
    }//GEN-LAST:event_none

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
      Update_tables();
    }//GEN-LAST:event_jTabbedPane1StateChanged
//Обработчик поля "Имя"
    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped
         if(jTextField1.getText().length()>=40){
            evt.consume();
        }
    }//GEN-LAST:event_jTextField1KeyTyped
//Ограничение длины фамилии
    private void jTextField2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyTyped
       if(jTextField2.getText().length()>=30){
            evt.consume();
        }
    }//GEN-LAST:event_jTextField2KeyTyped
//Поиск сотрудников по ФИО
    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        if(Employee.getSelectedRow()==-1){
        String SQL="{CALL Search_employee(?,?,?)}";
            Object[] parameters={jTextField1.getText(),jTextField2.getText(),jTextField3.getText()};
            String[] types={"String","String","String"};
            this.Employee.setModel(fill_table(make_call_select(SQL,types,parameters,11),0));
        }
    }//GEN-LAST:event_jTextField1KeyReleased
//переключатель на инженера
    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        switch_insert(1);
    }//GEN-LAST:event_jRadioButton1ActionPerformed
//Нажатие на кнопку добавления сотрудника
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

       boolean is_ready=jTextField1.getText().length()!=0&&jTextField2.getText().length()!=0&&jTextField3.getText().length()!=0&&
               jTextField4.getText().length()!=0&&jTextField5.getText().length()!=0&&jTextField6.getText().length()!=0&&
               jTextField7.getText().length()!=0;
       if(is_ready){
           jPanel10.setVisible(true);
       }
       else{
            String problem="<html>Не все поля для добавления заполнены <html>";
             ReportForm rep=new ReportForm(this,true,problem);
             rep.setVisible(true);
       }
       jRadioButton1.setSelected(true);
       switch_insert(1);
       jButton2.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed
//Ограничение ввода пола
    private void jTextField4KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyTyped
        String str=jTextField4.getText();
        if(str.length()>=1||evt.getKeyChar()!='М'||evt.getKeyChar()!='Ж'||evt.getKeyChar()!='м'||evt.getKeyChar()!='ж'){
            evt.consume();
        }
    }//GEN-LAST:event_jTextField4KeyTyped
//Ограничение длины отчества
    private void jTextField3KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyTyped
        if(jTextField3.getText().length()>=40){
            evt.consume();
        }
    }//GEN-LAST:event_jTextField3KeyTyped
//Стандартизация ввода пола
    private void jTextField4KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyReleased
       if(evt.getKeyChar()=='м'){
           jTextField4.setText("М");
        }
       if(evt.getKeyChar()=='ж'){
           jTextField4.setText("Ж");
       }
    }//GEN-LAST:event_jTextField4KeyReleased
//Корректные символы для даты + ограничение длины
    private void jTextField5KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyTyped
       String str=jTextField5.getText();
       char key=evt.getKeyChar();
       if(key<'0'||key>'9'||str.length()>=10){
           evt.consume();
       }
    }//GEN-LAST:event_jTextField5KeyTyped
//Корректный ввод даты
    private void jTextField5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyReleased
        String str=jTextField5.getText();
        char key=evt.getKeyChar();
        if(str.length()==2&&key!=KeyEvent.VK_BACK_SPACE){
            str+='-';
        }
        if(str.length()==5&&key!=KeyEvent.VK_BACK_SPACE){
            str+='-';
        }
        jTextField5.setText(str);
    }//GEN-LAST:event_jTextField5KeyReleased
//Корректное удаление символов из даты
    private void jTextField5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyPressed
        String str=jTextField5.getText();
        char key=evt.getKeyChar();
        if(str.length()==3&&key==KeyEvent.VK_BACK_SPACE){
            str=str.substring(0, 2);
        }
        if(str.length()==6&&key==KeyEvent.VK_BACK_SPACE){
            str=str.substring(0, 5);
        }
        jTextField5.setText(str);
    }//GEN-LAST:event_jTextField5KeyPressed
//Стандартный ограничитель ввода символов
    private void Anti_letters(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Anti_letters
      char key=evt.getKeyChar();
       if(key<'0'||key>'9'){
           evt.consume();
       }
    }//GEN-LAST:event_Anti_letters

    private void jTextField7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyPressed
       String str=jTextField7.getText();
        char key=evt.getKeyChar();
        if(str.length()==2&&key==KeyEvent.VK_BACK_SPACE){
            str=str.substring(0, 1);
        }
        jTextField7.setText(str);
                          
    }//GEN-LAST:event_jTextField7KeyPressed

    private void jTextField7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyReleased
        String str=jTextField7.getText();
        char key=evt.getKeyChar();
        if(str.length()==1&&key!=KeyEvent.VK_BACK_SPACE){
            str+='/';
        }
        jTextField7.setText(str);
    }//GEN-LAST:event_jTextField7KeyReleased

    private void jTextField7KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyTyped
       String str=jTextField7.getText();
       char key=evt.getKeyChar();
       if(key<'0'||key>'9'||str.length()>=3){
           evt.consume();
       }
    }//GEN-LAST:event_jTextField7KeyTyped

    private void EmployeeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EmployeeMouseClicked
        
        if(Employee.getSelectedRows().length>1){
            Employee.clearSelection();
            jButton2.setVisible(true);
            jPanel17.setVisible(false);
            Update_tables();
            evt.consume();
        }
        else{
        Object[] data=get_table_data(Employee.getModel(),Employee.getSelectedRow());
        jTextField1.setText(data[1].toString());
        jTextField2.setText(data[2].toString());
        jTextField3.setText(data[3].toString());
        jTextField4.setText(data[4].toString());
        jTextField5.setText(data[5].toString());
        jTextField6.setText(data[6].toString());
        jTextField7.setText(data[7].toString());
        jComboBox2.setSelectedItem(data[8]);
        jButton2.setVisible(false);
        jPanel17.setVisible(true);
        }
    }//GEN-LAST:event_EmployeeMouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        
        String SQL="{CALL Update_employee(?,?,?,?,?,?,?,?,?)}";
        Object[] parameters={jTextField1.getText(),jTextField2.getText(),jTextField3.getText(),//ФИО
            jTextField4.getText(),make_date(jTextField5.getText()),Integer.parseInt(jTextField6.getText()),//пол, дата рождения, зп
            jTextField7.getText(),jComboBox2.getSelectedItem().toString(),//режим работы + образование
            (int)Employee.getModel().getValueAt(Employee.getSelectedRow(), 0)};//id записи
        String[] types={"String","String","String",
            "String","Date","Int","String","String","Int"};
        make_call_change(SQL,types,parameters);
        Update_tables();
        Employee.clearSelection();
       
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
       switch_insert(3);
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
       switch_insert(4);
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jRadioButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton5ActionPerformed
           switch_insert(5);
    }//GEN-LAST:event_jRadioButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        Update_tables();
        jButton2.setVisible(true);
        jPanel10.setVisible(false);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
     boolean is_ready=jTextField8.getText().length()!=0&&jTextField10.getText().length()!=0&&
            (jTextField11.getText().length()!=0||!jTextField12.isVisible())&&
            (jTextField12.getText().length()!=0||!jTextField12.isVisible());
     if (is_ready){
        String SQL="{CALL Insert_employee(?,?,?,?,?,?,?,?)}";
        String[] types={"String","String","String","String","Date","Int","String","String"};
        Object[] parameters={ jTextField1.getText(),
        jTextField2.getText(),
        jTextField3.getText(),
        jTextField4.getText(),
        make_date(jTextField5.getText()),
        Integer.parseInt(jTextField6.getText()),//пол, дата рождения, зп
        jTextField7.getText(),
        jComboBox2.getSelectedItem().toString()
        };
        make_call_change(SQL,types,parameters);
        int emp=last_ins_id();
        SQL="{CALL Search_department(?,?,?,?)}";
        types=new String[]{"String","String","String","String"};
        DataForm get_dep=new DataForm(this,true,
                tables.getTableModel(8),
                false,SQL,login,password,types,
        "Выберите отдел",0);
        get_dep.setVisible(true);
        
        SQL="{CALL Set_employee_department(?,?)}";
        types=new String[]{"Int","Int"};
        parameters=new Object[]{emp,get_dep.selected_id};
        make_call_change(SQL,types,parameters);
        SQL="{CALL Insert_job_note(?,?,?)}";
        types=new String[]{"Int","String","Int"};
        if (jRadioButton1.isSelected()){
            parameters=new Object[]{emp,"Инженер",Integer.parseInt(jTextField8.getText())};
            make_call_change(SQL,types,parameters);
            emp=last_ins_id();
            SQL="{CALL Insert_engineer(?,?,?,?)}";
            types=new String[]{"String","String","String","Int"};
            parameters=new Object[]{jTextField10.getText(),jTextField11.getText(),jTextField12.getText(),emp};
            make_call_change(SQL,types,parameters);
        }
        if (jRadioButton2.isSelected()){
            parameters=new Object[]{emp,"Конструктор",Integer.parseInt(jTextField8.getText())};
            make_call_change(SQL,types,parameters);
            emp=last_ins_id();
            SQL="{CALL Insert_constructor(?,?,?,?)}";
            types=new String[]{"Int","String","String","Int"};
            parameters=new Object[]{Integer.parseInt(jTextField10.getText()),jTextField11.getText(),jTextField12.getText(),emp};
            make_call_change(SQL,types,parameters);
        }
        if (jRadioButton3.isSelected()){
            parameters=new Object[]{emp,"Лаборант",Integer.parseInt(jTextField8.getText())};
            make_call_change(SQL,types,parameters);
            emp=last_ins_id();
            SQL="{CALL Insert_lab_tech(?,?,?,?)}";
            types=new String[]{"Int","String","Int","Int"};
            parameters=new Object[]{Integer.parseInt(jTextField10.getText()),jTextField11.getText(),Integer.parseInt(jTextField12.getText()),emp};
            make_call_change(SQL,types,parameters);
        }
        if (jRadioButton4.isSelected()){
            parameters=new Object[]{emp,"Техник",Integer.parseInt(jTextField8.getText())};
            make_call_change(SQL,types,parameters);
            emp=last_ins_id();
            SQL="{CALL Insert_technician(?,?,?)}";
            types=new String[]{"Int","String","Int"};
            parameters=new Object[]{Integer.parseInt(jTextField10.getText()),jTextField11.getText(),emp};
            make_call_change(SQL,types,parameters);
        }
        if (jRadioButton5.isSelected()){
            parameters=new Object[]{emp,"Обслуживающий персонал",Integer.parseInt(jTextField8.getText())};
            make_call_change(SQL,types,parameters);
            emp=last_ins_id();
            SQL="{CALL Insert_serviceman(?,?)}";
            types=new String[]{"Int","String"};
            parameters=new Object[]{emp, jTextField10.getText()};
            make_call_change(SQL,types,parameters);
        }
        Update_tables();
        jPanel10.setVisible(false);
        
     }
     else{
         String problem="<html>Не все поля для добавления заполнены <html>";
             ReportForm rep=new ReportForm(this,true,problem);
             rep.setVisible(true);
     }
     
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
       String SQL="{CALL delete_employee(?)}";
        String[] types={"Int"};
        Object[] parameters={ (int)Employee.getModel().getValueAt(Employee.getSelectedRow(), 0)};
        make_call_change(SQL,types,parameters);
        Update_tables();
        Employee.clearSelection();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jRadioButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton6ActionPerformed
        if(this.Groups.getSelectedRow()!=-1){
            String SQL="{CALL Show_group(?)}";
            String[] types={"Int"};
            Object[] parameters={ (int)Groups.getModel().getValueAt(Groups.getSelectedRow(), 0)};
            this.GroupContent.setModel(fill_table(make_call_select(SQL,types,parameters,11),0));
        }
        this.jButton12.setVisible(true);
        this.jButton28.setVisible(false);
    }//GEN-LAST:event_jRadioButton6ActionPerformed

    private void jRadioButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton7ActionPerformed
        if(this.Groups.getSelectedRow()!=-1){
            String SQL="{CALL Show_group_equipment(?)}";
            String[] types={"Int"};
            Object[] parameters={ (int)Groups.getModel().getValueAt(Groups.getSelectedRow(), 0)};
            this.GroupContent.setModel(fill_table(make_call_select(SQL,types,parameters,7),11));
        }
        this.jButton12.setVisible(false);
        this.jButton28.setVisible(true);
    }//GEN-LAST:event_jRadioButton7ActionPerformed

    private void GroupsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_GroupsMouseClicked
     if(this.Groups.getSelectedRow()!=-1){
         Update_groups();
        }

    }//GEN-LAST:event_GroupsMouseClicked
private void Update_groups(){
    if(this.jRadioButton6.isSelected()){
            String SQL="{CALL Show_group(?)}";
            String[] types={"Int"};
            Object[] parameters={ (int)Groups.getModel().getValueAt(Groups.getSelectedRow(), 0)};
            this.GroupContent.setModel(fill_table(make_call_select(SQL,types,parameters,11),0));
            this.jButton12.setVisible(true);
            this.jButton28.setVisible(false);
         }
         else {
            String SQL="{CALL Show_group_equipment(?)}";
            String[] types={"Int"};
            Object[] parameters={ (int)Groups.getModel().getValueAt(Groups.getSelectedRow(), 0)};
            this.GroupContent.setModel(fill_table(make_call_select(SQL,types,parameters,7),11));
            this.jButton12.setVisible(false);
            this.jButton28.setVisible(true);
         }
}
    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        String SQL="{CALL Search_employee(?,?,?)}";
        String [] types=new String[]{"String","String","String"};
        DataForm get_emp=new DataForm(this,true,
                tables.getTableModel(0),
                false,SQL,login,password,types,
        "Выберите сотрудника",1);
        get_emp.setVisible(true);
        SQL="{CALL Set_employee_group(?,?)}";
        types=new String[]{"Int","Int"};
        Object[] parameters={get_emp.selected_id,(int)Groups.getModel().getValueAt(Groups.getSelectedRow(), 0)};
        make_call_change(SQL,types,parameters);
        Update_groups();
        
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
         String SQL="{CALL Search_equipment(?,?)}";
        String [] types=new String[]{"String","String"};
        DataForm get_equip=new DataForm(this,true,
                tables.getTableModel(11),
                false,SQL,login,password,types,
        "Выберите оборудование",2);
        get_equip.setVisible(true);
        SQL="{CALL Set_equipment_group(?,?)}";
        types=new String[]{"Int","Int"};
        Object[] parameters={get_equip.selected_id,(int)Groups.getModel().getValueAt(Groups.getSelectedRow(), 0)};
        make_call_change(SQL,types,parameters);
        Update_groups();
    }//GEN-LAST:event_jButton28ActionPerformed

    private void jTextField34KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField34KeyReleased
       String SQL="{CALL Search_manager(?,?,?,?,?,?,?)}";
       Object[] parameters={jTextField34.getText(),jTextField35.getText(),jTextField36.getText(),"",jTextField37.getText(),jTextField39.getText(),jTextField40.getText()};
       if(this.jRadioButton21.isSelected()){
           parameters[3]="Инженер";
       }
       if(this.jRadioButton22.isSelected()){
           parameters[3]="Конструктор";
       }
       if(this.jRadioButton23.isSelected()){
           parameters[3]="";
       }
       String[] types={"String","String","String","String","String","String","String"};
       this.Managers.setModel(fill_table(make_call_select(SQL,types,parameters,9),7));
        
    }//GEN-LAST:event_jTextField34KeyReleased

    private void jRadioButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton23ActionPerformed
          String SQL="{CALL Search_manager(?,?,?,?,?,?,?)}";
       Object[] parameters={jTextField34.getText(),jTextField35.getText(),jTextField36.getText(),"",jTextField37.getText(),jTextField39.getText(),jTextField40.getText()};
       if(this.jRadioButton21.isSelected()){
           parameters[3]="Инженер";
       }
       if(this.jRadioButton22.isSelected()){
           parameters[3]="Конструктор";
       }
       if(this.jRadioButton23.isSelected()){
           parameters[3]="";
       }
       String[] types={"String","String","String","String","String","String","String"};
       this.Managers.setModel(fill_table(make_call_select(SQL,types,parameters,9),7));
    }//GEN-LAST:event_jRadioButton23ActionPerformed

    private void First_2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_First_2ActionPerformed
        job_switch(1);
        Update_jobs();
    }//GEN-LAST:event_First_2ActionPerformed
private void Update_jobs(){
    if(this.First_2.isSelected()){
        String temp=jTextField19.getText();
        if(temp.equals("0")) jTextField19.setText("");
        temp=jTextField21.getText();
          if(temp.equals("0")) jTextField21.setText("");
            String SQL="{CALL Search_engineer(?,?,?,?,?,?)}";
            Object[] parameters={jTextField22.getText(),jTextField23.getText(),jTextField24.getText(),jTextField19.getText(),jTextField20.getText(),jTextField21.getText()};
            String[] types={"String","String","String","String","String","String"};
            this.Jobs.setModel(fill_table(make_call_select(SQL,types,parameters,10),1));
         }
   
    if(this.jRadioButton17.isSelected()){
        
         if(jTextField19.getText().length()==0) jTextField19.setText("0");
         String temp=jTextField21.getText();
          if(temp.equals("0")) jTextField21.setText("");
            String SQL="{CALL Search_constructor(?,?,?,?,?,?)}";
            Object[] parameters={jTextField22.getText(),jTextField23.getText(),jTextField24.getText(),Integer.parseInt(jTextField19.getText()),jTextField20.getText(),jTextField21.getText()};
            String[] types={"String","String","String","Int","String","String"};
            this.Jobs.setModel(fill_table(make_call_select(SQL,types,parameters,10),2));
         }
    if(this.jRadioButton18.isSelected()){
         if(jTextField19.getText().length()==0) jTextField19.setText("0");
         if(jTextField21.getText().length()==0) jTextField21.setText("0");
            String SQL="{CALL Search_lab_tech(?,?,?,?,?,?)}";
            Object[] parameters={jTextField22.getText(),jTextField23.getText(),jTextField24.getText(),Integer.parseInt(jTextField19.getText()),jTextField20.getText(),Integer.parseInt(jTextField21.getText())};
            String[] types={"String","String","String","Int","String","Int"};
            this.Jobs.setModel(fill_table(make_call_select(SQL,types,parameters,10),3));
         }
     if(this.jRadioButton19.isSelected()){
           if(jTextField19.getText().length()==0) jTextField19.setText("0");
            String SQL="{CALL Search_technician(?,?,?,?,?)}";
            Object[] parameters={jTextField22.getText(),jTextField23.getText(),jTextField24.getText(),Integer.parseInt(jTextField19.getText()),jTextField20.getText()};
            String[] types={"String","String","String","Int","String"};
            this.Jobs.setModel(fill_table(make_call_select(SQL,types,parameters,9),4));
         }
      if(this.jRadioButton20.isSelected()){
           String temp=jTextField19.getText();
            if(temp.equals("0")) jTextField19.setText("");
            String SQL="{CALL Search_serviceman(?,?,?,?)}";
            Object[] parameters={jTextField22.getText(),jTextField23.getText(),jTextField24.getText(),jTextField19.getText()};
            String[] types={"String","String","String","String"};
            this.Jobs.setModel(fill_table(make_call_select(SQL,types,parameters,8),5));
         }
       if(this.jRadioButton24.isSelected()){
            String SQL="{CALL Search_chief(?,?,?)}";
            Object[] parameters={jTextField22.getText(),jTextField23.getText(),jTextField24.getText()};
            String[] types={"String","String","String"};
            this.Jobs.setModel(fill_table(make_call_select(SQL,types,parameters,8),6));
         }
}
    private void jRadioButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton18ActionPerformed
       job_switch(3);
       Update_jobs();
    }//GEN-LAST:event_jRadioButton18ActionPerformed

    private void jRadioButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton19ActionPerformed
         job_switch(4);
         Update_jobs();
    }//GEN-LAST:event_jRadioButton19ActionPerformed

    private void jRadioButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton20ActionPerformed
         job_switch(5);
         Update_jobs();
    }//GEN-LAST:event_jRadioButton20ActionPerformed

    private void jRadioButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton24ActionPerformed
        job_switch(6);
        Update_jobs();
    }//GEN-LAST:event_jRadioButton24ActionPerformed

    private void jTextField19KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField19KeyReleased
        if(Jobs.getSelectedRow()==-1) Update_jobs();
    }//GEN-LAST:event_jTextField19KeyReleased

    private void JobsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JobsMouseClicked
        if(Jobs.getSelectedRows().length>1){
            Jobs.clearSelection();
            jButton15.setVisible(false);
            Update_jobs();
            job_switch(8);
            evt.consume();
            
        }
        else{
        Object[] data=get_table_data(Jobs.getModel(),Jobs.getSelectedRow());
        jTextField19.setText(data[7].toString());
        if(data.length>8)
        jTextField20.setText(data[8].toString());
        if(data.length>9)
        jTextField21.setText(data[9].toString());
        jButton15.setVisible(true);
        }       
    }//GEN-LAST:event_JobsMouseClicked

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        int job_id=get_job_id((int)Jobs.getModel().getValueAt(Jobs.getSelectedRow(), 0));
        if(this.First_2.isSelected()){
            String SQL="{CALL Update_engineer(?,?,?,?)}";
            Object[] parameters={jTextField19.getText(),jTextField20.getText(),jTextField21.getText(),job_id};
            String[] types={"String","String","String","Int"};
            make_call_change(SQL,types,parameters);
            Update_jobs();
            job_switch(8);
            
            
       }
        if(this.jRadioButton17.isSelected()){
            if(jTextField19.getText().length()==0) jTextField19.setText("0");
            String SQL="{CALL Update_constructor(?,?,?,?)}";
            Object[] parameters={Integer.parseInt(jTextField19.getText()),jTextField20.getText(),jTextField21.getText(),job_id};
            String[] types={"Int","String","String","Int"};
            make_call_change(SQL,types,parameters);
            Update_jobs();
            job_switch(8);
            
            
       }
        if(this.jRadioButton18.isSelected()){
            if(jTextField19.getText().length()==0) jTextField19.setText("0");
            if(jTextField21.getText().length()==0) jTextField21.setText("0");
            String SQL="{CALL Update_lab_tech(?,?,?,?)}";
            Object[] parameters={Integer.parseInt(jTextField19.getText()),jTextField20.getText(),Integer.parseInt(jTextField21.getText()),job_id};
            String[] types={"Int","String","Int","Int"};
            make_call_change(SQL,types,parameters);
            Update_jobs();
            job_switch(8);
            
            
       }
         if(this.jRadioButton19.isSelected()){
            if(jTextField19.getText().length()==0) jTextField19.setText("0");
            String SQL="{CALL Update_technician(?,?,?)}";
            Object[] parameters={Integer.parseInt(jTextField19.getText()),jTextField20.getText(),job_id};
            String[] types={"Int","String","Int"};
            make_call_change(SQL,types,parameters);
            Update_jobs();
            job_switch(8);
            
            
       }
          if(this.jRadioButton20.isSelected()){
        
            String SQL="{CALL Update_serviceman(?,?)}";
            Object[] parameters={jTextField19.getText(),job_id};
            String[] types={"String","Int"};
            make_call_change(SQL,types,parameters);
            Update_jobs();
            job_switch(8);
            
            
       }
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jTextField13KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField13KeyReleased
       if(this.Department.getSelectedRow()==-1){
        String SQL="{CALL Search_department(?,?,?,?)}";
            Object[] parameters={this.jTextField41.getText(),this.jTextField42.getText(),this.jTextField43.getText(),this.jTextField13.getText()};
            String[] types={"String","String","String","String"};
            this.Department.setModel(fill_table(make_call_select(SQL,types,parameters,6),8));
       }
    }//GEN-LAST:event_jTextField13KeyReleased

    private void DepartmentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_DepartmentMouseClicked
       if(Department.getSelectedRows().length>1){
           Department.clearSelection();
             this.jButton7.setVisible(true);
           this.jButton8.setVisible(false);
           this.jButton9.setVisible(false);
           this.jPanel23.setVisible(false);
           Clear_textboxes(3);
           evt.consume();
       }
       else{
           Object[] data=get_table_data(Department.getModel(),Department.getSelectedRow());
           jTextField13.setText(data[5].toString());
           this.jButton7.setVisible(false);
           this.jButton8.setVisible(true);
           this.jButton9.setVisible(true);
           this.jPanel23.setVisible(true);
       }
    }//GEN-LAST:event_DepartmentMouseClicked

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
       String SQL="{CALL delete_department(?)}";
            Object[] parameters={Department.getModel().getValueAt(Department.getSelectedRow(), 0)};
            String[] types={"Int"};
            make_call_change(SQL,types,parameters);
            Clear_textboxes(3);
            Update_tables();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
         String SQL="{CALL engineers_and_constructors(?,?,?,?,?)}";
            String[] types={"String","String","String","String","String"};
            DataForm get_ch=new DataForm(this,true,
                tables.getTableModel(14),
                false,SQL,login,password,types,
        "Выберите начальника",3);
        get_ch.setVisible(true);
       
        int job_id=get_job_id(get_ch.selected_id);
        SQL="{CALL delete_constructor(?)}";
        types=new String[]{"Int"};
        Object[] parameters=new Object[]{job_id};
        make_call_change(SQL, types, parameters);
        SQL="{CALL delete_engineer(?)}";
        make_call_change(SQL, types, parameters);
        
        SQL="{CALL Update_job_note(?,?,?)}";
        types=new String[]{"String","Int","Int"};
        parameters=new Object[]{"Начальник отдела",0,get_ch.selected_id};
        make_call_change(SQL, types, parameters);
        
        SQL="{CALL Insert_chief(?,?)}";
        types=new String[]{"Int","Date"};
        parameters=new Object[]{job_id,new Date(System.currentTimeMillis())};
        make_call_change(SQL, types, parameters);
        
        
        SQL="{CALL Insert_department(?,?)}";
        types=new String[]{"Int","String"};
        parameters=new Object[]{last_ins_id(),jTextField13.getText()};
        make_call_change(SQL, types, parameters);
        SQL="{CALL Set_employee_department(?,?)}";
        types=new String[]{"Int","Int"};
        parameters=new Object[]{get_ch.selected_id,last_ins_id()};
        make_call_change(SQL, types, parameters);
        Clear_textboxes(3);
        Update_tables();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
         String SQL="{CALL Update_department(?,?)}";
            Object[] parameters={this.jTextField13.getText(),Department.getModel().getValueAt(Department.getSelectedRow(), 0)};
            String[] types={"String","Int"};
            make_call_change(SQL,types,parameters);
            Clear_textboxes(3);
            Update_tables();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
      String SQL="{CALL Search_employee(?,?,?)}";
        String [] types=new String[]{"String","String","String"};
        DataForm get_emp=new DataForm(this,true,
                tables.getTableModel(0),
                false,SQL,login,password,types,
        "Выберите сотрудника",1);
        get_emp.setVisible(true);
       SQL="{CALL Set_employee_department(?,?)}";
        types=new String[]{"Int","Int"};
        Object[] parameters=new Object[]{get_emp.selected_id,Department.getModel().getValueAt(Department.getSelectedRow(), 0)};
        make_call_change(SQL, types, parameters);
        Update_tables();
    }//GEN-LAST:event_jButton22ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
         String SQL="{CALL Search_equipment(?,?)}";
        String [] types=new String[]{"String","String"};
        DataForm get_equip=new DataForm(this,true,
                tables.getTableModel(11),
                false,SQL,login,password,types,
        "Выберите оборудование",2);
        get_equip.setVisible(true);
        SQL="{CALL Set_equipment_department(?,?)}";
        types=new String[]{"Int","Int"};
        Object[] parameters=new Object[]{get_equip.selected_id,Department.getModel().getValueAt(Department.getSelectedRow(), 0)};
        make_call_change(SQL, types, parameters);
        Update_tables();
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        String SQL="{CALL Show_department(?)}";
        String [] types=new String[]{"Int"};
        Object[] parameters=new Object[]{Department.getModel().getValueAt(Department.getSelectedRow(), 0)};
        DataForm get_emp=new DataForm(this,true,
                tables.getTableModel(0),
                false,SQL,login,password,types,
        "Просмотр сотрудников",4, parameters);
         get_emp.setVisible(true);
    }//GEN-LAST:event_jButton29ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
       String SQL="{CALL Show_department_equipment(?)}";
        String [] types=new String[]{"Int"};
        Object[] parameters=new Object[]{Department.getModel().getValueAt(Department.getSelectedRow(), 0)};
        DataForm get_emp=new DataForm(this,true,
                tables.getTableModel(11),
                false,SQL,login,password,types,
        "Просмотр оборудования",4, parameters);
         get_emp.setVisible(true);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       int j_type=jComboBox1.getSelectedIndex();
       if(j_type==0){
           JournalForm journal=new JournalForm("journal_employee",tables.getTableModel(15),login,password);
           journal.setVisible(true);
       }
       if(j_type==1){
           JournalForm journal=new JournalForm("journal_equipment",tables.getTableModel(16),login,password);
           journal.setVisible(true);
       }
       if(j_type==2){
           JournalForm journal=new JournalForm("journal_projects",tables.getTableModel(18),login,password);
           journal.setVisible(true);
       }
       if(j_type==3){
           JournalForm journal=new JournalForm("journal_contracts",tables.getTableModel(17),login,password);
           journal.setVisible(true);
       }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void EquipmentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_EquipmentMouseClicked
    if(Equipment.getSelectedRows().length>1){
            Equipment.clearSelection();
            jButton19.setVisible(true);
            jButton20.setVisible(false);
            jButton21.setVisible(false);
            Update_tables();
            Clear_textboxes(7);
            evt.consume();
        }
        else{
        Object[] data=get_table_data(Equipment.getModel(),Equipment.getSelectedRow());
        jTextField14.setText(data[1].toString());
        jTextField15.setText(data[2].toString());
        jTextField16.setText(data[3].toString());
        jTextField17.setText(data[4].toString());
        jButton19.setVisible(false);
        jButton20.setVisible(true);
        jButton21.setVisible(true);
        }      
    }//GEN-LAST:event_EquipmentMouseClicked

    private void jTextField14KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField14KeyReleased
       if(this.Equipment.getSelectedRow()==-1){
        String SQL="{CALL Search_equipment(?,?)}";
            Object[] parameters={this.jTextField14.getText(),this.jTextField15.getText()};
            String[] types={"String","String"};
            this.Equipment.setModel(fill_table(make_call_select(SQL,types,parameters,7),11));
       }
    }//GEN-LAST:event_jTextField14KeyReleased

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
      String SQL="{CALL Insert_equipment(?,?,?,?)}";
        Object[] parameters={jTextField14.getText(),jTextField15.getText(),Integer.parseInt(jTextField16.getText()),Integer.parseInt(jTextField17.getText()),
           };
        String[] types={"String","String","Int","Int",};
        make_call_change(SQL,types,parameters);
        Update_tables();
        Equipment.clearSelection();  
        Clear_textboxes(7);
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
       String SQL="{CALL delete_equipment(?)}";
        String[] types={"Int"};
        Object[] parameters={ (int)Equipment.getModel().getValueAt(Equipment.getSelectedRow(), 0)};
        make_call_change(SQL,types,parameters);
        Update_tables();
        Equipment.clearSelection();
        Clear_textboxes(7);
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
    String SQL="{CALL Update_equipment(?,?,?,?,?)}";
        Object[] parameters={jTextField14.getText(),jTextField15.getText(),Integer.parseInt(jTextField16.getText()),
            Integer.parseInt(jTextField17.getText()),
            (int)Equipment.getModel().getValueAt(Equipment.getSelectedRow(), 0)};
        String[] types={"String","String","Int","Int","Int"};
        make_call_change(SQL,types,parameters);
        Update_tables();
        Equipment.clearSelection();  
        Clear_textboxes(7);
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
       String SQL="{CALL Update_contract(?,?)}";
        Object[] parameters={make_date(jTextField44.getText()),
            (int)Contract.getModel().getValueAt(Contract.getSelectedRow(), 0)};
        String[] types={"Date","Int"};
        make_call_change(SQL,types,parameters);
        SQL="{CALL Update_сustomer(?,?,?)}";
        parameters = new Object[]{jTextField45.getText(),jTextField46.getText(),
            get_customer_id((int)Contract.getModel().getValueAt(Contract.getSelectedRow(), 0))};
        types=new String[]{"String","String","Int"};
        make_call_change(SQL,types,parameters);
        Update_tables();
        Equipment.clearSelection();  
        Clear_textboxes(4);
    
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton25ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        String SQL="{CALL Show_projects(?)}";
        String [] types=new String[]{"Int"};
        Object[] parameters=new Object[]{Contract.getModel().getValueAt(Contract.getSelectedRow(), 0)};
        DataForm get_emp=new DataForm(this,true,
                tables.getTableModel(19),
                false,SQL,login,password,types,
        "Просмотр проектов",4, parameters);
         get_emp.setVisible(true);       
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton30ActionPerformed

    private void ContractMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ContractMouseClicked
        if(Contract.getSelectedRows().length>1){
            Contract.clearSelection();
            jButton10.setVisible(true);
            jButton13.setVisible(false);
            jButton14.setVisible(false);
             jButton27.setVisible(false);
            Update_tables();
            Clear_textboxes(7);
            evt.consume();
        }
        else{
        Object[] data=get_table_data(Contract.getModel(),Contract.getSelectedRow());
        jTextField44.setText(data[3].toString());
        jTextField45.setText(data[4].toString());
        jTextField46.setText(data[5].toString());
        jButton10.setVisible(false);
            jButton13.setVisible(true);
            jButton14.setVisible(true);
            jButton27.setVisible(true);
        }      
    }//GEN-LAST:event_ContractMouseClicked

    private void jTextField45KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField45KeyReleased
        if(Contract.getSelectedRow()==-1){
        String SQL="{CALL Search_contract(?,?,?,?,?)}";
            Object[] parameters={jTextField57.getText(),jTextField58.getText(),jTextField59.getText(),jTextField45.getText(),jTextField46.getText()};
            String[] types={"String","String","String","String","String"};
            this.Contract.setModel(fill_table(make_call_select(SQL,types,parameters,11),9));
        }
    }//GEN-LAST:event_jTextField45KeyReleased

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        String SQL="{CALL Insert_customer(?,?)}";
        Object[] parameters={jTextField45.getText(),jTextField46.getText()};
        String[] types={"String","String"};
        make_call_change(SQL,types,parameters);
        int cust=last_ins_id();
        SQL="{CALL engineers_and_constructors(?,?,?,?,?)}";
            types=new String[]{"String","String","String","String","String"};
            DataForm get_man=new DataForm(this,true,
                tables.getTableModel(14),
                false,SQL,login,password,types,
        "Выберите руководителя",3);
        get_man.setVisible(true);
        int emp=get_job_id(get_man.selected_id);
        boolean who=en_or_con(get_man.selected_id);
        if(who){
            emp=get_eng_id(emp);
        }
        else{
             emp=get_con_id(emp);
        }
        SQL="{CALL Insert_manager(?,?,?)}";
        parameters = new Object[]{"Договор",emp,who};
        types=new String[]{"String","Int","Bool"};
        make_call_change(SQL,types,parameters);
        int man=last_ins_id();
         SQL="{CALL Insert_contract(?,?,?,?,?)}";
        parameters = new Object[]{0,0,make_date(jTextField44.getText()),cust,man};
        types=new String[]{"Int","Int","Date","Int","Int"};
        make_call_change(SQL,types,parameters);
        Update_tables();
        Contract.clearSelection();  
        Clear_textboxes(4);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        String SQL="CALL delete_manager(?)";
        String[] types={"Int"};
        Object[] parameters={(int)Contract.getModel().getValueAt(Contract.getSelectedRow(), 7)};
        make_call_change(SQL,types,parameters);
        SQL="CALL get_managers_contract(?)";
        parameters=new Object[]{(int)Contract.getModel().getValueAt(Contract.getSelectedRow(), 0)};
        ArrayList<Object[]> temp=this.make_call_select(SQL, types, parameters, 1);
        SQL="CALL delete_manager(?)";
        for(int i=0;i<temp.size();i++){
            this.make_call_change(SQL, types, temp.get(i));
        }
        SQL="CALL delete_customer(?)";
        parameters=new Object[]{get_customer_id((int)Contract.getModel().getValueAt(Contract.getSelectedRow(), 0))};
        make_call_change(SQL,types,parameters);
        Update_tables();
        Contract.clearSelection();  
        Clear_textboxes(4);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
       String SQL="{CALL engineers_and_constructors(?,?,?,?,?)}";
            String[] types=new String[]{"String","String","String","String","String"};
            DataForm get_man=new DataForm(this,true,
                tables.getTableModel(14),
                false,SQL,login,password,types,
        "Выберите руководителя",3);
            get_man.setVisible(true);
        int emp=get_job_id(get_man.selected_id);
        boolean who=en_or_con(get_man.selected_id);
        if(who){
            emp=get_eng_id(emp);
        }
        else{
             emp=get_con_id(emp);
        }
        SQL="{CALL Insert_manager(?,?,?)}";
        Object[] parameters = new Object[]{"Проект",emp,who};
        types=new String[]{"String","Int","Bool"};
        make_call_change(SQL,types,parameters);
        int man=last_ins_id();
        SQL="{CALL Search_contract(?,?,?,?,?)}";
        types=new String[]{"String","String","String","String","String"};
        DataForm get_con=new DataForm(this,true,
                tables.getTableModel(9),
                false,SQL,login,password,types,
        "Выберите договор",5);
        get_con.setVisible(true);
        SQL="{CALL Insert_project(?,?,?,?,?)}";
        parameters = new Object[]{make_date(jTextField47.getText()),Integer.parseInt(jTextField48.getText()),
            Integer.parseInt(jTextField49.getText()),get_con.selected_id,man};
        types=new String[]{"Date","Int","Int","Int","Int"};
        make_call_change(SQL,types,parameters);
        Update_tables();
        Contract.clearSelection();  
        Clear_textboxes(5);
            
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
       String SQL="{CALL Finish_project(?)}";
       String[] types={"Int"};
       Object[] parameters={(int)Project.getModel().getValueAt(Project.getSelectedRow(), 0)};
       make_call_change(SQL,types,parameters);
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        String SQL="{CALL Search_project(?,?,?)}";
        String[] types=new String[]{"String","String","String"};
            DataForm get_pr=new DataForm(this,true,
                tables.getTableModel(14),
                false,SQL,login,password,types,
        "Выберите проект",1);
           get_pr.setVisible(true);
         SQL="{CALL Insert_subcontractor(?,?,?,?,?,?,?)}";
         types=new String[]{"String","String","String","Int","Int","Date","Int"};
         Object[] parameters={jTextField18.getText(),jTextField29.getText(),jTextField28.getText(),Integer.parseInt(jTextField25.getText()),Integer.parseInt(jTextField26.getText()), 
             make_date(jTextField25.getText()),get_pr.selected_id};
         make_call_change(SQL,types,parameters);
        Update_tables();
    }//GEN-LAST:event_jButton24ActionPerformed
    ////////////////////////////////////////////////////////////////////////////////
    /*Функция работы */
////////////////////////////////////////////////////////////////////////////////
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable Contract;
    private javax.swing.JTable Department;
    private javax.swing.JTable Employee;
    private javax.swing.JTable Equipment;
    private javax.swing.JRadioButton First_2;
    private javax.swing.JTable GroupContent;
    private javax.swing.JTable Groups;
    private javax.swing.JTable Jobs;
    private javax.swing.JTable Managers;
    private javax.swing.JTable Project;
    private javax.swing.JTable Subcontractors;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton17;
    private javax.swing.JRadioButton jRadioButton18;
    private javax.swing.JRadioButton jRadioButton19;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton20;
    private javax.swing.JRadioButton jRadioButton21;
    private javax.swing.JRadioButton jRadioButton22;
    private javax.swing.JRadioButton jRadioButton23;
    private javax.swing.JRadioButton jRadioButton24;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField27;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField29;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField34;
    private javax.swing.JTextField jTextField35;
    private javax.swing.JTextField jTextField36;
    private javax.swing.JTextField jTextField37;
    private javax.swing.JTextField jTextField39;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField40;
    private javax.swing.JTextField jTextField41;
    private javax.swing.JTextField jTextField42;
    private javax.swing.JTextField jTextField43;
    private javax.swing.JTextField jTextField44;
    private javax.swing.JTextField jTextField45;
    private javax.swing.JTextField jTextField46;
    private javax.swing.JTextField jTextField47;
    private javax.swing.JTextField jTextField48;
    private javax.swing.JTextField jTextField49;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField57;
    private javax.swing.JTextField jTextField58;
    private javax.swing.JTextField jTextField59;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField60;
    private javax.swing.JTextField jTextField61;
    private javax.swing.JTextField jTextField62;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables
}
