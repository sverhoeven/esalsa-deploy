package nl.esciencecenter.esalsa.deploy.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import nl.esciencecenter.esalsa.deploy.StoreableObject;
import nl.esciencecenter.esalsa.deploy.server.SimpleStub;

@SuppressWarnings("serial")
public abstract class Viewer<T extends StoreableObject> extends JPanel implements ActionListener {

	private final static int SPACER = 5;
	
	protected RemoteStore<T> store;
	protected final SimpleStub stub;
	protected final RootPanel parent;
	
	private final JPanel formPanel;
	protected LinkedHashMap<String, EditorField> elements = new LinkedHashMap<String, EditorField>();
	
	private final JPanel buttonPanel;
	private final HashMap<String, ButtonHandler> buttonActions = new HashMap<String, ButtonHandler>();
	
	protected Viewer(RootPanel parent, SimpleStub stub, RemoteStore<T> store, boolean editable) { 
		
		super(new BorderLayout(SPACER, SPACER));
		
		this.store = store;
		this.parent = parent;
		this.stub = stub;

		formPanel = new JPanel();
		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.PAGE_AXIS));
		formPanel.setBorder(new EmptyBorder(5, 5, 5, 5));		
		formPanel.add(Box.createRigidArea(new Dimension(0, Utils.gapHeight)));
		
	    addField(new TextLineField("ID", editable));
		addField(new TextAreaField("Comment", "Comment", true, editable, -1, 5*Utils.defaultFieldHeight));
		
		JPanel container = new JPanel(new BorderLayout());
		container.add(formPanel, BorderLayout.NORTH);

		JScrollPane scrollPane = new JScrollPane(container,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		add(scrollPane, BorderLayout.CENTER);
		
		buttonPanel = new JPanel();
	    buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	    
		add(buttonPanel, BorderLayout.SOUTH);		
	}

	protected void addField(EditorField elt) {
		
		if (elt == null) { 
			return;
		}
		
		elements.put(elt.key, elt);
		formPanel.add(elt);
		formPanel.add(Box.createRigidArea(new Dimension(0, Utils.gapHeight)));
	}
	
	protected void addButton(String command, ButtonHandler handler) {
		
		if (command == null) { 
			throw new IllegalArgumentException("Cannot add a button without a name!");
		}
		
		if (buttonActions.containsKey(command)) { 
			throw new IllegalArgumentException("Panel already contains button with name \"" + command + "\"!");
		}
		
		JButton button = new JButton(command);
		
		button.setActionCommand(command);;
	    button.addActionListener(this);
	    buttonActions.put(command, handler);
		buttonPanel.add(button);
	}
	
	protected void setElementValue(String key, Object value) { 
		
		if (key == null) { 
			return;
		}
		
		EditorField field = elements.get(key);
		
		if (field == null)  { 
			return;
		}
		
		field.setValue(value);
	}
	
	protected Object getElementValue(String key) { 
		
		if (key == null) { 
			return null;
		}
		
		EditorField field = elements.get(key);
		
		if (field == null)  { 
			return null;
		}
		
		return field.getValue();
	}
	
	protected void clear() {
		
		if (elements.size() == 0) { 
			return;
		}
 		
		for (EditorField ef : elements.values()) { 
			ef.clear();
		}
	}
	
	public void show(String ID) {
		
		if (ID == null) { 
			return;
		}
	
		T elt = null;
		
		try { 
			elt = store.get(ID);
		} catch (Exception ex) {
			System.err.println("Failed to show! " + ex.getLocalizedMessage());
			ex.printStackTrace(System.err);
		} 
		
		if (elt == null) { 
			return;
		}

		show(elt);
	}
	
	public void actionPerformed(ActionEvent e) {		
		
		// One of the buttons was clicked!
		String command = e.getActionCommand();
		
		ButtonHandler handler = buttonActions.get(command);
				
		if (handler == null) { 
			System.err.println("No handler found for button \"" + command +"\"!");
			return;
		}
		
		handler.clicked();		
	}
	
	protected void showError(String message, Exception e) { 		
		System.err.println(message);
		System.err.println(e.getLocalizedMessage());		
		e.printStackTrace(System.err);
		
		JOptionPane.showMessageDialog(this, message + "\n(" + e.getLocalizedMessage() + ")");
	}
	
	protected void showMessage(String message) { 		
		JOptionPane.showMessageDialog(this, message);
	}
	
	protected boolean askConfirmation(String message) { 		
		
		String [] options = new String [] { "OK", "Cancel" };
		String selected = "Cancel";
		
		int result = JOptionPane.showOptionDialog(this, message, "WARNING", JOptionPane.OK_CANCEL_OPTION, 
				JOptionPane.WARNING_MESSAGE, null, options, selected);

		return (result == 0);
	}
	
	protected abstract void show(T elt);	
}
