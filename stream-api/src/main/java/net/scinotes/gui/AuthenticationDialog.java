/*
 *  Copyright (C) 2007,2008 Christian Bockermann <chris@jwall.org>
 *
 *  This file is part of the  AuditViewer. You can get more information
 *  about  AuditViewer  on its web page at
 *
 *               http://www.jwall.org/web/audit/viewer.jsp
 *
 *  AuditViewer  is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AuditViewer  is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.scinotes.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


/**
 * 
 * @author chris@jwall.org
 */
public class AuthenticationDialog extends Dialog 
    implements KeyListener, ActionListener
{
    public final static long serialVersionUID = 12L;
	private String login;
	private String pass;
	
	private JTextField loginField = new JTextField();
	private JPasswordField passField = new JPasswordField();
	private JCheckBox sv = new JCheckBox("save password");
	private JButton okButton;
    private JButton cancelButton;
    
	public AuthenticationDialog()
	{
        setTitle("Authenticate");
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		setResizable( false );
        
		JPanel p = new JPanel();
        p.setLayout(null);
        p.setSize(new Dimension(200, 100));
        p.setMinimumSize(new Dimension(200, 100));
        p.setMaximumSize(new Dimension(200, 100));
        
        int x0 = 20;
        int y0 = 30;
        
        JLabel l = new JLabel("Login");
        l.setBounds(x0, y0, 60, 20);
        p.add(l);
        
        l = new JLabel("Password");
        l.setBounds(x0, y0 + 25, 60, 20);
        p.add(l);
        
        loginField = new JTextField(25);
        loginField.addKeyListener(this);
        loginField.setBounds(110, y0, 161, 20);
        p.add(loginField);
        
        passField = new JPasswordField();
        passField.addKeyListener( this );
        passField.setBounds(110, y0 + 25, 161, 20);
        p.add(passField);

        sv.setBounds(110, y0 + 50, 160, 20);
        p.add( sv );
        
        URL url = AuthenticationDialog.class.getResource( "/icons/logo_tall_60x513.jpg" );
        ImagePanel i = new ImagePanel( url, 0, 0, 60, 513);
        getContentPane().add(i, BorderLayout.WEST);
		
		this.getContentPane().add(p, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		okButton = new JButton("Ok");
        okButton.setEnabled( false );
		okButton.addActionListener(this);
		buttonPanel.add(okButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);

		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
		setSize(new Dimension(400,180));
		center();
	}
    
    public void doOk(){
        pass = new String(passField.getPassword());
        login = loginField.getText();
        setVisible(false);
    }

	public void setPassword(String pass){
	    this.pass = pass;
	    passField.setText(pass);
	}
	
	public void setLogin(String login){
	    this.login = login;
	    loginField.setText(login);
	    passField.requestFocus();
	}
    
    public void setSavePassword(boolean b){
        sv.setSelected( b );
    }
	
	public String getLogin(){ return this.login; }
	public String getPassword(){ return this.pass; }
    public boolean savePassword(){ return sv.isSelected(); }
    
    public boolean checkInput(){
        return ! (passField.getPassword().length == 0 || "".equals(loginField.getText()));
    }

    public void keyPressed(KeyEvent arg0)
    {
    }

    public void keyReleased(KeyEvent arg0)
    {
    }

    public void keyTyped(KeyEvent arg0)
    {
        okButton.setEnabled( checkInput() );
    }
    
    public void doCancel(){
        login = null;
        pass = null;
        setVisible(false);
    }
    
    public void actionPerformed(ActionEvent e){
        if( e.getSource() == this.okButton )
            doOk();
        
        if( e.getSource() == this.cancelButton ){
            doCancel();
        }
    }
    
    
    public static String[] authenticate(){
    	AuthenticationDialog d = new AuthenticationDialog();
    	d.setVisible( true );
    	
    	if( System.getProperty( "scinotes.username" ) != null ){
    		d.setLogin( System.getProperty( "scinotes.username" ) );
    	} else {
    		d.setLogin( System.getProperty( "user.name" ) );
    	}
    	
    	if( d.getLogin() == null || d.getPassword() == null )
    		return null;
    	
    	return new String[]{ d.getLogin(), d.getPassword() };
    }
    
    
    public static void main(String args[]){
        AuthenticationDialog d = new AuthenticationDialog();
        d.setVisible(true);
        
        System.out.println("login = "+d.getLogin());
        System.out.println("passw = "+d.getPassword());
        System.exit(0);
    }
}