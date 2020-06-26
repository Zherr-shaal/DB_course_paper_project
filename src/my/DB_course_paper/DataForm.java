/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.DB_course_paper;
import java.sql.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import javax.swing.table.*;

/**
 *
 * @author Matt
 */
public class DataForm extends javax.swing.JDialog {//Форма приёма данных у пользователя
    String SQL;
    String[] signature;
    DefaultTableModel base_table;
    public int selected_id;
    Connection connection;
    /**
     * Creates new form DataForm
     */
    public DataForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    public DataForm(java.awt.Frame parent, boolean modal,DefaultTableModel table_type, boolean rbuttons,String sql,String log,String pass, String[] sign,String mess,int type) {
        super(parent, modal);
        initComponents();
        base_table=table_type;
        jPanel1.setVisible(rbuttons);
        jButton10.setVisible(false);
        jLabel1.setText(mess);
        SQL=sql;
        signature=sign;
        initConnection(log,pass);
        first_view();
        prepare_form();
        set_titles(type);
        
    }
    public DataForm(java.awt.Frame parent, boolean modal,DefaultTableModel table_type, boolean rbuttons,String sql,String log,String pass, String[] sign,String mess,int type,Object[] params) {
        super(parent, modal);
        initComponents();
        base_table=table_type;
        jPanel1.setVisible(rbuttons);
        jButton10.setVisible(false);
        jLabel1.setText(mess);
        SQL=sql;
        signature=sign;
        initConnection(log,pass);
        first_view(params);
        prepare_form();
        set_titles(type);
        
    }
    private void first_view(){
        Object[] parameters=parse_signature();
        this.jTable4.setModel(fill_table(make_call_select(SQL,signature,parameters,base_table.getColumnCount())));
    }
     private void first_view(Object[] parameters){
        this.jTable4.setModel(fill_table(make_call_select(SQL,signature,parameters,base_table.getColumnCount())));
    }
    private void prepare_form(){
        if(signature.length<5){
            this.jTextField48.setVisible(false);
            this.jLabel56.setVisible(false);
            if(signature.length<4){
                this.jTextField47.setVisible(false);
                this.jLabel55.setVisible(false);
                if(signature.length<3){
                    this.jTextField46.setVisible(false);
                    this.jLabel54.setVisible(false);
                    if(signature.length<2){
                    this.jTextField45.setVisible(false);
                    this.jLabel53.setVisible(false);
                    this.jButton10.setText("Прекратить просмотр");
                    }
                }
            }
        }
    }
    private void set_titles(int type){
        if(type==0){
            this.jLabel52.setText("Имя начальника");
            this.jLabel53.setText("Фамилия");
            this.jLabel54.setText("Отчество");
            this.jLabel55.setText("Направление деятельности отдела");
        }
        if(type==1){
            this.jLabel52.setText("Имя сотрудника");
            this.jLabel53.setText("Фамилия");
            this.jLabel54.setText("Отчество");
        }
        if(type==2){
            this.jLabel52.setText("Тип оборудования");
            this.jLabel53.setText("Производитель оборудования");
        }
        if(type==3){
            this.jLabel52.setText("Имя");
            this.jLabel53.setText("Фамилия");
            this.jLabel54.setText("Отчество");
            this.jLabel55.setText("Образование");
            this.jLabel56.setText("Научная степень");
        }
        if(type==4){
            jPanel21.setVisible(false);
        }
        if(type==5){
            this.jLabel52.setText("Имя руководителя");
            this.jLabel53.setText("Фамилия");
            this.jLabel54.setText("Отчество");
            this.jLabel55.setText("Заказчик");
            this.jLabel56.setText("Телефон заказчика");
        }
        
    }
    private void initConnection(String login,String password){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/db_course_paper?useUnicode=true&serverTimezone=Asia/Yekaterinburg", login, password);
            }
            catch(Exception ex){
             String problem="<html>Ошибка подключения!<br>Проблема: "+ex.getMessage()+"<html>";
             System.out.println(problem);
            }
    }
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
                if(type_parameters[i]=="date"){
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
        }
        return res;
    }
    private DefaultTableModel fill_table(ArrayList<Object[]> data){
        DefaultTableModel table=base_table;
        table.setRowCount(0);
        for(int i=0;i<data.size();i++){
            table.insertRow(table.getRowCount(), data.get(i));
        }
        return table;
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
    private Object[] parse_signature(){
        int size=signature.length;
        Object[] parameters=new Object[size];
        String[] values={this.jTextField44.getText(),
           this.jTextField45.getText(),
           this.jTextField46.getText(),
           this.jTextField47.getText(),
           this.jTextField48.getText()};
        for(int i=0;i<size;i++){
           if(signature[i]=="Int"){
               if(values[i].length()!=0) parameters[i]=Integer.parseInt(values[i]);
               else parameters[i]=0;
           }
           if(signature[i]=="String"){
               parameters[i]=values[i];
           }
           if(signature[i]=="Date"){
               parameters[i]=make_date(values[i]);
           }
         
       }
         return parameters;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jPanel21 = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        jTextField44 = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        jTextField45 = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        jTextField46 = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jTextField47 = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        jTextField48 = new javax.swing.JTextField();
        jButton11 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jRadioButton16 = new javax.swing.JRadioButton();
        jRadioButton17 = new javax.swing.JRadioButton();
        jRadioButton18 = new javax.swing.JRadioButton();
        jRadioButton19 = new javax.swing.JRadioButton();
        jRadioButton20 = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ввод данных");

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTable4.getTableHeader().setReorderingAllowed(false);
        jTable4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable4MouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(jTable4);

        jLabel52.setText("Атрибут 1");

        jTextField44.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField44KeyReleased(evt);
            }
        });

        jLabel53.setText("Атрибут 2");

        jTextField45.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField44KeyReleased(evt);
            }
        });

        jLabel54.setText("Атрибут 3");

        jTextField46.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField44KeyReleased(evt);
            }
        });

        jLabel55.setText("Атрибут 4");

        jTextField47.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField44KeyReleased(evt);
            }
        });

        jLabel56.setText("Атрибут 5");

        jTextField48.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField44KeyReleased(evt);
            }
        });

        jButton11.setText("Снять выделение");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel54)
                    .addComponent(jLabel53)
                    .addComponent(jLabel52)
                    .addComponent(jLabel55))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField44, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField45, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField46, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField47, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel21Layout.createSequentialGroup()
                        .addComponent(jLabel56)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jTextField48, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))
                .addContainerGap())
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel55)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel56)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        buttonGroup1.add(jRadioButton16);
        jRadioButton16.setText("Инженер");

        buttonGroup1.add(jRadioButton17);
        jRadioButton17.setText("Конструктор");
        jRadioButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton17ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton18);
        jRadioButton18.setText("Лаборант");

        buttonGroup1.add(jRadioButton19);
        jRadioButton19.setText("Техник");

        buttonGroup1.add(jRadioButton20);
        jRadioButton20.setText("Обслуживающий персонал");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jRadioButton16)
                    .addComponent(jRadioButton18)
                    .addComponent(jRadioButton17)
                    .addComponent(jRadioButton19)
                    .addComponent(jRadioButton20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jRadioButton16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton17)
                .addGap(3, 3, 3)
                .addComponent(jRadioButton18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton20)
                .addContainerGap(104, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel1.setText("jLabel1");

        jButton10.setText("Вввести данные");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 1200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(116, 116, 116)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(jButton10)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton17ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton17ActionPerformed

    private void jTable4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable4MouseClicked
       if(jTable4.getSelectedRows().length>1){
           jButton10.setVisible(false);
            jTable4.clearSelection();
            evt.consume();
        }
       else{
           jButton10.setVisible(true);
       }
    }//GEN-LAST:event_jTable4MouseClicked

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        selected_id=(int)jTable4.getModel().getValueAt(jTable4.getSelectedRow(), 0);
        this.setVisible(false);
        dispose();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jTextField44KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField44KeyReleased
       if(this.jTable4.getSelectedRow()==-1){

        Object[] parameters=parse_signature();
        this.jTable4.setModel(fill_table(make_call_select(SQL,signature,parameters,base_table.getColumnCount())));
        }
    }//GEN-LAST:event_jTextField44KeyReleased

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
       this.jTable4.clearSelection();
       jButton10.setVisible(false);
    }//GEN-LAST:event_jButton11ActionPerformed

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
            java.util.logging.Logger.getLogger(DataForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DataForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DataForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DataForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DataForm dialog = new DataForm(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JRadioButton jRadioButton16;
    private javax.swing.JRadioButton jRadioButton17;
    private javax.swing.JRadioButton jRadioButton18;
    private javax.swing.JRadioButton jRadioButton19;
    private javax.swing.JRadioButton jRadioButton20;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable4;
    private javax.swing.JTextField jTextField44;
    private javax.swing.JTextField jTextField45;
    private javax.swing.JTextField jTextField46;
    private javax.swing.JTextField jTextField47;
    private javax.swing.JTextField jTextField48;
    // End of variables declaration//GEN-END:variables
}
