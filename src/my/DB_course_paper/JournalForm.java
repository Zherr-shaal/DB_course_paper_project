/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.DB_course_paper;

import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Matt
 */
public class JournalForm extends javax.swing.JFrame {//Форма просмотра журналов
    DefaultTableModel base_table;
    Connection connection;
    String journal;
    /**
     * Creates new form JournalForm
     */
    public JournalForm() {
        initComponents();
    }
    public JournalForm(String type,DefaultTableModel table,String log,String pass) {
        initComponents();
        initConnection(log,pass);
        journal=type;
        base_table=table;
        first_view();
    }
    private void first_view(){
        String SQL=make_SQL("Search","_date(?,?,?)");
        String [] types=new String[]{"Date","Date","String"};
        Object[] parameters=new Object[]{make_date("01-01-1970"),make_date("01-01-2050"),""};
        this.jTable1.setModel(fill_table(make_call_select(SQL,types,parameters,base_table.getColumnCount())));
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
    private void make_call_change(String SQL,String[] type_parameters,Object[] parameters){
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
            call.executeUpdate();
        }
        catch(Exception ex){
             String problem="<html>Ошибка выполнения запроса!<br>Проблема: "+ex.getMessage()+"<html>";
             ReportForm rep=new ReportForm(this,true,problem);
             rep.setVisible(true);
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
    private String make_SQL(String proc,String params){
        return "{CALL "+proc+"_"+journal+params+"}";
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
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel21 = new javax.swing.JPanel();
        jLabel52 = new javax.swing.JLabel();
        jTextField44 = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        jTextField45 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jButton14 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        jLabel52.setText("Начальная дата");

        jTextField44.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField44KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField44KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField44KeyTyped(evt);
            }
        });

        jLabel53.setText("Конечная дата");

        jTextField45.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField45KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField45KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField45KeyTyped(evt);
            }
        });

        jButton10.setText("Найти записи");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField44, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jTextField45)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel53)
                    .addComponent(jLabel52))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton10)
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jButton12.setText("Удалить записи");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText("Закрыть");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jCheckBox1.setText("Все записи");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.setText("Добавление");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jCheckBox3.setText("Изменение");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jCheckBox4.setText("Удаление");
        jCheckBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jButton14.setText("Сбросить флажки");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jCheckBox1)
                            .addComponent(jCheckBox2)
                            .addComponent(jCheckBox3)
                            .addComponent(jCheckBox4))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 11, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox3)
                        .addGap(4, 4, 4)
                        .addComponent(jCheckBox4)
                        .addGap(7, 7, 7)
                        .addComponent(jButton14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton13)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        this.setVisible(false);
        dispose();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        this.jCheckBox2.setSelected(jCheckBox1.isSelected());
        this.jCheckBox3.setSelected(jCheckBox1.isSelected());
        this.jCheckBox4.setSelected(jCheckBox1.isSelected());
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
      jCheckBox1.setSelected(jCheckBox2.isSelected()&&jCheckBox3.isSelected()&&jCheckBox4.isSelected());
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jTextField44KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField44KeyPressed
       String str=jTextField44.getText();
        char key=evt.getKeyChar();
        if(str.length()==3&&key==KeyEvent.VK_BACK_SPACE){
            str=str.substring(0, 2);
        }
        if(str.length()==6&&key==KeyEvent.VK_BACK_SPACE){
            str=str.substring(0, 5);
        }
        jTextField44.setText(str);
    }//GEN-LAST:event_jTextField44KeyPressed

    private void jTextField44KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField44KeyTyped
        String str=jTextField44.getText();
       char key=evt.getKeyChar();
       if(key<'0'||key>'9'||str.length()>=10){
           evt.consume();
       }
    }//GEN-LAST:event_jTextField44KeyTyped

    private void jTextField44KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField44KeyReleased
         String str=jTextField44.getText();
        char key=evt.getKeyChar();
        if(str.length()==2&&key!=KeyEvent.VK_BACK_SPACE){
            str+='-';
        }
        if(str.length()==5&&key!=KeyEvent.VK_BACK_SPACE){
            str+='-';
        }
        jTextField44.setText(str);
    }//GEN-LAST:event_jTextField44KeyReleased

    private void jTextField45KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField45KeyPressed
        String str=jTextField45.getText();
        char key=evt.getKeyChar();
        if(str.length()==3&&key==KeyEvent.VK_BACK_SPACE){
            str=str.substring(0, 2);
        }
        if(str.length()==6&&key==KeyEvent.VK_BACK_SPACE){
            str=str.substring(0, 5);
        }
        jTextField45.setText(str);
    }//GEN-LAST:event_jTextField45KeyPressed

    private void jTextField45KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField45KeyReleased
        String str=jTextField45.getText();
        char key=evt.getKeyChar();
        if(str.length()==2&&key!=KeyEvent.VK_BACK_SPACE){
            str+='-';
        }
        if(str.length()==5&&key!=KeyEvent.VK_BACK_SPACE){
            str+='-';
        }
        jTextField45.setText(str);
    }//GEN-LAST:event_jTextField45KeyReleased

    private void jTextField45KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField45KeyTyped
         String str=jTextField45.getText();
       char key=evt.getKeyChar();
       if(key<'0'||key>'9'||str.length()>=10){
           evt.consume();
       }
    }//GEN-LAST:event_jTextField45KeyTyped

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        String SQL=make_SQL("Search","_date(?,?,?)");
        String [] types=new String[]{"Date","Date","String"};
        Object[] parameters=new Object[]{make_date(jTextField44.getText()),make_date(jTextField45.getText()),""};
        this.jTable1.setModel(fill_table(make_call_select(SQL,types,parameters,base_table.getColumnCount())));
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
         this.jCheckBox1.setSelected(false);
        this.jCheckBox2.setSelected(jCheckBox1.isSelected());
        this.jCheckBox3.setSelected(jCheckBox1.isSelected());
        this.jCheckBox4.setSelected(jCheckBox1.isSelected());
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        String type="";
        if (jCheckBox2.isSelected()){
            type="I";
        }
        if (jCheckBox3.isSelected()){
            type="U";
        }
        if (jCheckBox4.isSelected()){
            type="D";
        }
        if (jCheckBox1.isSelected()){
            type="A";
        }
        String SQL=make_SQL("Truncate","(?)");
        String [] types=new String[]{"String"};
        Object[] parameters=new Object[]{type};
        make_call_change(SQL, types, parameters);
        first_view();
    }//GEN-LAST:event_jButton12ActionPerformed

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
            java.util.logging.Logger.getLogger(JournalForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JournalForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JournalForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JournalForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JournalForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField44;
    private javax.swing.JTextField jTextField45;
    // End of variables declaration//GEN-END:variables
}
