/*
 * Created on 13.12.2004
 *
 */
package org.QTM.app;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.QTM.control.GfxLabel;
import org.QTM.control.IconCache;
import org.QTM.control.LiveSashForm;
import org.QTM.control.ToolTip;
import org.QTM.control.ToolTipHandler;
import org.QTM.data.HintNewPlayer;
import org.QTM.data.HintRemovedPlayer;
import org.QTM.data.Player;
import org.QTM.data.Result;
import org.QTM.data.Round;
import org.QTM.data.Seating;
import org.QTM.data.Tournament;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author WAHL_O
 * 
 */

// TODO Break up in different composites for clearar handling
// TODO Convert to JFace TableViewer/Handling
// TODO Change to JfaceForms?

public class Application implements Observer, DisposeListener {
	private Controller controller;

	private Shell sShell = null; //  @jve:decl-index=0:visual-constraint="10,10"

	private Display display;

	private LiveSashForm sashFormRow = null;
	private CLabel standingsTitle;
	private CLabel roundTitle;

	private ToolBar toolBar1 = null;

	private Text tournamentName = null;

	private Text field_TournamentLocation = null;

	private Table tablePlayer = null;

	private Table tableStandings = null;

	private Table tableSeatings = null;

	private CLabel roundTimer;

	private Color colorOrange;
	private Color colorRed;

	private Color colorLabelDark;
	private Color colorLabelLight;
	
	public Application(Display d, Controller c) {
		display = d;
		controller = c;

		controller.getCurrentTournament().addObserver(this);
	}

	void run() {
		sShell.open();
		update(controller.getCurrentTournament(), controller.getCurrentTournament());
		
		while (!sShell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

	}

	void center() {
		// TODO handle secondary (4x) monitors as well
		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = sShell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		sShell.setLocation(x, y);
	}

	/**
	 * This method initializes sShell
	 */
	void createSShell() {
		sShell = new Shell();
		sShell.setLayout(new FillLayout(SWT.VERTICAL));

		createComposite();

		sShell.setText(VersionInfo.getName()+ " " + VersionInfo.getVersion());
		sShell.setImage(IconCache.getImage(display, "Q Bubble 16x16.gif"));
		
		restoreWindowBounds();
		
		sShell.addDisposeListener(this);
	}
	
	  public void restoreWindowBounds() {
		Rectangle rect = PreferenceLoader.loadRectangle("windowBounds");

		if (rect == null) {
			center();
			return;
		}
		Rectangle bounds = display.getBounds();
		Rectangle intersect = rect.intersection(bounds);

		if (intersect.height < 50 || intersect.width < 50) {
			center();
			return;
		}
		sShell.setBounds(intersect);

		if (PreferenceLoader.loadBoolean("windowMaximized"))
			sShell.setMaximized(true);
	}

	private void createComposite() {
		colorOrange = new Color(display, 255,189,0);
		colorRed = new Color(display, 248,11,16);
		colorLabelDark = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND);
		colorLabelLight = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);

		Composite composite = new Composite(sShell, SWT.NONE);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		createSashFormRow(composite);
	}

	private void createSashFormRow(Composite parent) {
		sashFormRow = new LiveSashForm(parent, SWT.VERTICAL);
		GridData layout = new GridData(GridData.FILL_BOTH);
		sashFormRow.setLayoutData(layout);

		createTopRow(sashFormRow);
		createBottomRow(sashFormRow);
	}

	private void createTopRow(Composite parent) {
		Composite top_row = new Composite(parent, SWT.NONE);
		top_row.setLayout(new FillLayout());

		LiveSashForm sashForm = new LiveSashForm(top_row, SWT.HORIZONTAL);
		createTopLeft(sashForm);
		createTopRight(sashForm);
		
		sashForm.setWeights(new int[] { 50, 50 });
	}

	private void createBottomRow(Composite parent) {
		Composite bottom_row = new Composite(parent, SWT.NONE);
		bottom_row.setLayout(new FillLayout());

		LiveSashForm sashForm = new LiveSashForm(bottom_row, SWT.NONE);
		createBottomLeft(sashForm);
		createBottomRight(sashForm);

		sashForm.setWeights(new int[] { 50, 50 });
	}

	private void resize_column(LiveSashForm container, boolean left) {
		int new_weights[] = new int[2];

		Rectangle col = container.getClientArea();
		Integer col_cont; 
		if(left)
			col_cont= (Integer)container.getData("RIGHT_MIN_WIDTH");
		else
			col_cont= (Integer)container.getData("LEFT_MIN_WIDTH");
			
		if (left) {
			new_weights[1] = col_cont.intValue() + 8;
			new_weights[0] = col.width - new_weights[1];
		} else {
			new_weights[0] = col_cont.intValue() + 8;
			new_weights[1] = col.width - new_weights[0];
		}
		container.setWeights(new_weights);
	}

	private void resize_row(LiveSashForm container, boolean top) {
		int new_weights[] = new int[2];

		Rectangle row = container.getClientArea();
		Integer row_cont = (Integer)container.getData("MIN_HEIGHT");

		if (top) {
			new_weights[0] = row.height - row_cont.intValue();
			new_weights[1] = row_cont.intValue();
		} else {
			new_weights[0] = row_cont.intValue();
			new_weights[1] = row.height - row_cont.intValue();
		}
		container.setWeights(new_weights);
	}

	private void createTopLeft(final LiveSashForm parent) {
		Composite top_left = new Composite(parent, SWT.NONE);
		parent.setChildBorder(top_left, SWT.SHADOW_OUT);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 2;
		top_left.setLayout(layout);

		GfxLabel label = new GfxLabel(top_left, SWT.NONE, colorLabelLight, colorLabelDark);
		GridData gid = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER);
		gid.horizontalSpan = 2;
		label.setLayoutData(gid);

		label.setText("Tournament");

		Point size = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		parent.setData("LEFT_MIN_WIDTH", new Integer(size.x) );
		sashFormRow.setData("MIN_HEIGHT", new Integer(size.y) );

		label.addMouseListener(new MouseListener() {

			// on double-click expand to only show tournament part
			public void mouseDoubleClick(MouseEvent e) {
				resize_row(sashFormRow, true);
				resize_column(parent, true);

				tournamentName.setFocus();
			}

			public void mouseDown(MouseEvent e) {

			}

			public void mouseUp(MouseEvent e) {

			}

		});

		Label separator = new Label(top_left, SWT.HORIZONTAL | SWT.SEPARATOR);
		gid = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		gid.horizontalSpan = 2;
		separator.setLayoutData(gid);

		createToolBar(top_left);

		Label l = new Label(top_left, SWT.RIGHT);
		l.setText("Name:");
		gid = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		gid.horizontalIndent = 8;
		l.setLayoutData(gid);

		tournamentName = new Text(top_left, SWT.BORDER);
		gid = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER);
		tournamentName.setLayoutData(gid);
		tournamentName.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				controller.getCurrentTournament().setName(tournamentName.getText());
			}

		});

		l = new Label(top_left, SWT.RIGHT);
		l.setText("Location:");
		gid = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		gid.horizontalIndent = 8;
		l.setLayoutData(gid);

		field_TournamentLocation = new Text(top_left, SWT.BORDER);
		gid = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		field_TournamentLocation.setLayoutData(gid);

		field_TournamentLocation.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				String s = field_TournamentLocation.getText();
				controller.getCurrentTournament().setLocation(s);
				
				// TODO does not work on press of toolbar button?!?
			}

		});
	
		// TODO add max rounds
		// TODO add time limit on round
		// TODO add k-value of tournament
		// TODO coordinator PIN and name
		// TODO (basically add all data necessary for ERS)
		// TODO add cut to top 8 on seatings menu
		// TODO pick random player from standings
		// TODO pick random seating from seatings for deck check
	}

	private void createTopRight(final LiveSashForm parent) {
		Composite top_right = new Composite(parent, SWT.NONE);
		parent.setChildBorder(top_right, SWT.SHADOW_OUT);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		top_right.setLayout(layout);

		CLabel label = new GfxLabel(top_right, SWT.NONE, colorLabelDark, colorLabelLight);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER);
		label.setLayoutData(gridData);
		label.setText("Player");
		
		Point size = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		parent.setData("RIGHT_MIN_WIDTH", new Integer(size.x) );
		sashFormRow.setData("MIN_HEIGHT", new Integer(size.y) );

		label.addMouseListener(new MouseListener() {

			// on double-click expand to only show tournament part
			public void mouseDoubleClick(MouseEvent e) {
				resize_row(sashFormRow, true);
				resize_column(parent, false);
			}

			public void mouseDown(MouseEvent e) {

			}

			public void mouseUp(MouseEvent e) {

			}

		});

		Label separator = new Label(top_right, SWT.HORIZONTAL | SWT.SEPARATOR);
		gridData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_CENTER);
		createPlayerTable(top_right);
		separator.setLayoutData(gridData);
	}

	private void createBottomLeft(final LiveSashForm parent) {
		Composite bottom_left = new Composite(parent, SWT.NONE);
		parent.setChildBorder(bottom_left, SWT.SHADOW_OUT);

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		bottom_left.setLayout(layout);

		standingsTitle = new GfxLabel(bottom_left, SWT.NONE, colorLabelDark, colorLabelLight);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		standingsTitle.setLayoutData(gridData);
		
		standingsTitle.setText("Standings");

		Point size = standingsTitle.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		parent.setData("LEFT_MIN_WIDTH", new Integer(size.x) );
		sashFormRow.setData("MIN_HEIGHT", new Integer(size.y) );
		
		standingsTitle.addMouseListener(new MouseListener() {

			// on double-click expand to only show tournament part
			public void mouseDoubleClick(MouseEvent e) {
				resize_row(sashFormRow, false);
				resize_column(parent, true);
			}

			public void mouseDown(MouseEvent e) {

			}

			public void mouseUp(MouseEvent e) {

			}

		});

		Label separator = new Label(bottom_left, SWT.HORIZONTAL | SWT.SEPARATOR);
		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		separator.setLayoutData(gridData);

		createStandingsTable(bottom_left);
	}


	private void createBottomRight(final LiveSashForm parent) {
		Composite bottom_right = new Composite(parent, SWT.NONE);
		parent.setChildBorder(bottom_right, SWT.SHADOW_OUT);

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.makeColumnsEqualWidth = false;
		bottom_right.setLayout(layout);

		roundTitle = new GfxLabel(bottom_right, SWT.NONE, colorLabelDark, colorLabelLight);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.CENTER;
		
		roundTitle.setLayoutData(gridData);
		roundTitle.setText("Round");

		roundTitle.addMouseListener(new MouseListener() {

			// on double-click expand to only show tournament part
			public void mouseDoubleClick(MouseEvent e) {
				resize_row(sashFormRow, false);
				resize_column(parent, false);
			}

			public void mouseDown(MouseEvent e) {

			}

			public void mouseUp(MouseEvent e) {

			}

		});

		roundTimer = new GfxLabel(bottom_right, SWT.CENTER, colorLabelLight, colorLabelDark);
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.CENTER;
		roundTimer.setLayoutData(gridData);
		
		roundTimer.setText("00:00");

		Point size1 = roundTitle.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point size2 = roundTimer.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		parent.setData("RIGHT_MIN_WIDTH", new Integer(size1.x + size2.x) );
		sashFormRow.setData("MIN_HEIGHT", new Integer(Math.max(size1.y, size2.y)) );
	
		Label separator = new Label(bottom_right, SWT.HORIZONTAL | SWT.SEPARATOR);
		gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		gridData.horizontalSpan = 2;
		separator.setLayoutData(gridData);

		createSeatingsTable(bottom_right);
	}

	/**
	 * This method initializes toolBar2
	 *  
	 */
	private void createToolBar(Composite parent) {
		toolBar1 = new ToolBar(parent, SWT.FLAT | SWT.BORDER);
		ToolItem tool_NewTournament = new ToolItem(toolBar1, SWT.PUSH);
		tool_NewTournament.setText("New");
		
		final Application app = this;
		
		tool_NewTournament.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				controller.newTournament(sShell, app);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		ToolItem tool_OpenTournament = new ToolItem(toolBar1, SWT.PUSH);
		tool_OpenTournament.setText("Open");
		tool_OpenTournament.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {

				controller.openTournament(sShell, app);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		GridData gid = new GridData();
		gid.horizontalAlignment = GridData.FILL;
		gid.verticalAlignment = GridData.BEGINNING;
		gid.horizontalSpan = 2;
		toolBar1.setLayoutData(gid);
	}

	private Menu createPlayerMenu() {
		Menu popUpMenu = new Menu(sShell, SWT.POP_UP);

		/**
		 * Adds a listener to handle enabling and disabling some items in the
		 * Edit submenu.
		 */
		popUpMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu menu = (Menu) e.widget;
				MenuItem[] items = menu.getItems();

				items[0].setEnabled(!controller.getCurrentTournament().hasStarted()); // new

				items[2].setEnabled(!controller.getCurrentTournament().hasStarted() && tablePlayer.getSelectionCount() == 1); // delete
				items[4].setEnabled(tablePlayer.getItemCount() != 0); // find
			}
		});

		//New

		MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("New");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				controller.getCurrentTournament().addPlayer("New", 1);
			}
		});

		new MenuItem(popUpMenu, SWT.SEPARATOR);

		//Delete

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Delete");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = tablePlayer.getSelection();
				if (items.length == 0)
					return;

				controller.getCurrentTournament().removePlayer((Player) items[0].getData());
			}
		});

		new MenuItem(popUpMenu, SWT.SEPARATOR);

		//Find...

		item = new MenuItem(popUpMenu, SWT.NULL);
		item.setText("Find");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO searchDialog.open();
			}
		});

		item = new MenuItem(popUpMenu, SWT.NULL);
		item.setText("Copy to local players DB");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// TODO copy to local players DB();
			}
		});

		return popUpMenu;
	}

	/**
	 * This method initializes tablePlayer
	 *  
	 */
	private void createPlayerTable(Composite parent) {
		tablePlayer = new Table(parent, SWT.FULL_SELECTION | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);

		final TableColumn colPlayerName = new TableColumn(tablePlayer, SWT.LEFT);
		colPlayerName.setText("Name");

		final TableColumn colPlayerDCI = new TableColumn(tablePlayer, SWT.RIGHT);
		colPlayerDCI.setText("DCI");

		// TODO handle multiple select
		// TODO add player TID (Tournament ID) for secure results entry
		
		GridData gid = new GridData();
		gid.horizontalAlignment = GridData.FILL;
		gid.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gid.horizontalSpan = 2;
		gid.grabExcessVerticalSpace = true;
		tablePlayer.setLayoutData(gid);

		tablePlayer.setHeaderVisible(true);
		tablePlayer.setLinesVisible(true);

		tablePlayer.setMenu(createPlayerMenu());

		tablePlayer.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = tablePlayer.getClientArea();
				Point preferredSize = tablePlayer.computeSize(SWT.DEFAULT,
						SWT.DEFAULT);
				int width = area.width - 2 * tablePlayer.getBorderWidth();
				if (preferredSize.y > area.height) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = tablePlayer.getVerticalBar().getSize();
					width -= vBarSize.x;
				}
				int columns = 10;
				GC gc = new GC(tablePlayer);
				Point min_colwidth = gc.textExtent("000000000000");
				gc.dispose();

				if (width > 2 * min_colwidth.x)
					colPlayerDCI.setWidth(min_colwidth.x);
				else
					colPlayerDCI.setWidth(0);
				colPlayerName.setWidth(width - colPlayerDCI.getWidth());
			}
		});

		// TODO change to combobox controls
		// TODO add type ahead search in local players
		// TODO fill combobox dynamically on typeahed (and dropdown if less than 10 items)
		// TODO check double entry of players (same name? 
		final TableEditor editor = new TableEditor(tablePlayer);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		tablePlayer.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				if (event.button != 1)
					return;

				Rectangle clientArea = tablePlayer.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = tablePlayer.getTopIndex();
				while (index < tablePlayer.getItemCount()) {
					boolean visible = false;
					final TableItem item = tablePlayer.getItem(index);
					for (int i = 0; i < tablePlayer.getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							final int column = i;
							final Text text = new Text(tablePlayer, SWT.NONE);
							
							Listener textListener = new Listener() {
								public void handleEvent(final Event e) {
									Player p = (Player) item.getData();

									if(e.type == SWT.Traverse && e.detail == SWT.TRAVERSE_ESCAPE)
										e.doit = false;
									
									if(e.doit)
									{
										switch (column) {
										case 0:
											controller.getCurrentTournament().changePlayerName(p, text.getText());
											break;
										case 1:
											controller.getCurrentTournament().changePlayerDCI(p, Integer.parseInt(text.getText()));
											break;
										}
									}	
									text.dispose();
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							text.addListener(SWT.Deactivate, textListener);

							if (i == 1) {
								text.setTextLimit(10);
								text.addVerifyListener(new VerifyListener() {
									public void verifyText(VerifyEvent e) {
										// If backspace or delete button is
										// pressed, then no validation is
										// required.

										if ((e.character == SWT.DEL)
												|| (e.character == SWT.BS)) {
											return;
										}
										if (e.character == '\0'
												|| Character
														.isDigit(e.character)) {
											e.doit = true;
										} else {
											e.doit = false;
										}
									}
								});
							}
							editor.setEditor(text, item, i);
							text.setText(item.getText(i));
							text.selectAll();
							text.setFocus();
													
							return;
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});
	}

	public void update(Observable o, Object arg) {
		Tournament t = (Tournament)o;
		
		updateTournament(t, arg);
		updatePlayers(t, arg);
		updateSeatings(t, arg);
		updateStandings(t, arg);
	}

	private void updateTournament(Tournament t, Object hint) {
		if (hint instanceof Tournament) {
			String name = t.getName();

			if (name != null)
				tournamentName.setText(name);
			else
				tournamentName.setText("");

			if (t.getLocation() != null)
				field_TournamentLocation.setText(t.getLocation());
			else
				field_TournamentLocation.setText("");

			if (name == null || "".equals(name))
				name = "QTM SWT";
			else
				name = "QTM SWT: " + name;

			if (t.hasChanged())
				name = "* " + name;

			sShell.setText(name);

		}
	}
	
	private void updatePlayers(Tournament t, Object hint) {
		if( hint instanceof HintRemovedPlayer)
		{
			Player p = ((HintRemovedPlayer)hint).getPlayer();
			
			TableItem[] oldItems = tablePlayer.getItems();
			for (int i = 0; i < oldItems.length; i++) {
				if (oldItems[i].getData() == p) {
					oldItems[i].dispose();
					break;
				}
			}
			
		} else if (hint instanceof HintNewPlayer) // added player
		{
			Player p = ((HintNewPlayer) hint).getPlayer();
			TableItem ti = new TableItem(tablePlayer, SWT.NULL);
			ti.setData(p);
			tablePlayer.setSelection(tablePlayer.getItemCount() - 1);

			String[] values = { p.getName(), Integer.toString(p.getDCI()) };
			ti.setText(values);

		} else if (hint instanceof Player) // added/changed this player
		{
			Player p = (Player) hint;
			TableItem ti = null;

			TableItem[] oldItems = tablePlayer.getItems();
			for (int i = 0; i < oldItems.length; i++) {
				if (oldItems[i].getData() == p) {
					ti = oldItems[i];
					break;
				}
			}

			if (ti == null) {
				// something wrong here
				System.out.println("Missing player in table!");
				return;
			}

			String[] values = { p.getName(), Integer.toString(p.getDCI()) };
			ti.setText(values);

		} else if (hint instanceof Round) {
		} else if (hint instanceof Seating) {
		} else if (hint instanceof HintElapsedRoundTime) {
		} else if (hint instanceof Tournament) {
			// remove old cruft
			tablePlayer.clearAll();

			TableItem[] oldItems = tablePlayer.getItems();
			for (int i = 0; i < oldItems.length; i++)
				oldItems[i].dispose();

			Iterator it = t.getPlayers().iterator();

			while (it.hasNext()) {
				Player p = (Player) it.next();
				String[] values = { p.getName(), Integer.toString(p.getDCI()),
						Integer.toString(p.getMatchPoints()) };

				TableItem ti = new TableItem(tablePlayer, SWT.NULL);
				ti.setText(values);
				ti.setData(p);
			}

		}
	}

	private void playerItems(TableItem ti, Player p, int rank) {
		String[] values = new String[7];

		values[0] = Integer.toString( rank );
		values[1] = p.getName();
		values[2] = Integer.toString(p.getMatchPoints());
		values[3] = Double.toString(p.printableOpponentScore()) + "%";
		values[4] = Double.toString(p.printableGameWinPercentage()) + "%";
		values[5] = Double.toString(p.printableOpponentGameWinPercentage()) + "%";
		values[6] = Integer.toString(p.getMatchesPlayed()) + "/"
				+ Integer.toString(p.getMatchesWon()) + "/"
				+ Integer.toString(p.getMatchesLost()) + "/"
				+ Integer.toString(p.getMatchesDrawn()) + "/"
				+ Integer.toString(p.getMatchesBye());

		ti.setText(values);

		if(ti.getData() != p)
		{
			ti.setData(p);
			ti.setData(ToolTip.TOOLTIP_TITLE, "Detailed standings" );
		}
		ti.setData(ToolTip.TOOLTIP_TEXT, p.printableStanding() );
	}

	private void updateStandingsTitle() {
		if(controller.getCurrentTournament().isRoundFinished())
			standingsTitle.setText("Final Standings");
		else
			standingsTitle.setText("Provisional Standings");
	}
	
	private void updateStandings(Tournament t, Object hint) {
		if( hint instanceof HintRemovedPlayer)
		{
			Player p = ((HintRemovedPlayer)hint).getPlayer();
			
			TableItem[] oldItems = tableStandings.getItems();
			boolean removed = false;
			for (int i = 0; i < oldItems.length; i++) {
				Player fp = (Player) oldItems[i].getData();
				if (fp == p) {
					oldItems[i].dispose();
					removed = true;
				}
				else if (removed)
				{
					playerItems(oldItems[i], fp, i);					
				}
			}
		} else if (hint instanceof HintNewPlayer)
		{
			Player p = ((HintNewPlayer)hint).getPlayer();

			int rank = controller.getCurrentTournament().getPlayerRank(p);
			TableItem ti = new TableItem(tableStandings, SWT.NULL, rank - 1);				

			playerItems(ti, p, rank);

			// fix all other players rank
			TableItem[] oldItems = tableStandings.getItems();
			for (int i = 0; i < oldItems.length; i++) {
				Player fp = (Player) oldItems[i].getData();
				
				if(fp != p)
					playerItems(oldItems[i], fp, i+1);
			}

			updateStandingsTitle();
		} else if (hint instanceof Player)
		{
			Player p = (Player) hint;
			int i;
			TableItem ti = null; 
			
			TableItem[] oldItems = tableStandings.getItems();
			for (i = 0; i < oldItems.length; i++) {
				if (oldItems[i].getData() == p) {
					ti = oldItems[i];
					break;
				}
			}

			if(ti == null)
			{
				System.out.println("Missing player in standings table");
				return;
			}
			
			int rank = controller.getCurrentTournament().getPlayerRank(p);
			if(i != rank-1)
			{	
				ti.dispose();
				ti = new TableItem(tableStandings, SWT.NULL, rank - 1);
			}
			playerItems(ti, p, rank);

			// fix all other players rank
			oldItems = tableStandings.getItems();
			for (i = 0; i < oldItems.length; i++) {
				Player fp = (Player) oldItems[i].getData();
				
				if(fp != p)
					playerItems(oldItems[i], fp, i+1);
			}

			updateStandingsTitle();
		} else if (hint instanceof Tournament || hint instanceof Round) {
			List playersRanked = t.getPlayersRanked();
			TableItem[] oldItems = tableStandings.getItems();

			// e.g. after loading/new tournament
			if( playersRanked.size() < oldItems.length)
			{
				// remove old cruft
				tableStandings.clearAll();
	
				for (int i = playersRanked.size(); i < oldItems.length; i++)
				{
					oldItems[i].dispose();
					oldItems[i] = null;
				}
				
			}
			
			Iterator it = playersRanked.iterator();

			while (it.hasNext()) {
				Player p = (Player) it.next();

				int rank = playersRanked.indexOf(p) + 1;

				// use table item at correct position!
				TableItem ti = null;
				
				if(rank-1 < oldItems.length)
					ti = oldItems[rank-1];

				// handle empty 
				if(ti == null)
					ti = new TableItem(tableStandings, SWT.NULL, rank - 1);
				
				playerItems(ti, p, rank);
			}
			
			updateStandingsTitle();
		}
	}

	private void createStandingsTable(Composite parent) {
		tableStandings = new Table(parent, SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);

		GridData gid = new GridData();
		gid.horizontalAlignment = GridData.FILL;
		gid.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gid.horizontalSpan = 2;
		gid.grabExcessVerticalSpace = true;
		tableStandings.setLayoutData(gid);

		tableStandings.setHeaderVisible(true);
		tableStandings.setLinesVisible(true);

		final TableColumn colRank = new TableColumn(tableStandings, SWT.CENTER);
		colRank.setText("Rank");
		colRank.setWidth(40);

		final TableColumn colName = new TableColumn(tableStandings, SWT.LEFT);
		colName.setText("Name");
		colName.setWidth(100);

		final TableColumn colPoints = new TableColumn(tableStandings, SWT.RIGHT);
		colPoints.setText("Points");
		colPoints.setWidth(50);

		final TableColumn colOppMatch = new TableColumn(tableStandings, SWT.RIGHT);
		colOppMatch.setText("Opp Win%");
		colOppMatch.setWidth(75);

		final TableColumn colPLGame = new TableColumn(tableStandings, SWT.RIGHT);
		colPLGame.setText("Game Win%");
		colPLGame.setWidth(75);

		final TableColumn colOppGame = new TableColumn(tableStandings, SWT.RIGHT);
		colOppGame.setText("OppGame%");
		colOppGame.setWidth(75);

		final TableColumn colMatches = new TableColumn(tableStandings, SWT.LEFT);
		colMatches.setText("P/W/L/D/B");
		colMatches.setWidth(80);
		
		tableStandings.setMenu( createStandingsMenu() );
		
		ToolTipHandler toolTipHandler = new ToolTipHandler(tableStandings);
		
		tableStandings.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = tableStandings.getClientArea();
				Point preferredSize = tableStandings.computeSize(SWT.DEFAULT,
						SWT.DEFAULT);
				int width = area.width - 2 * tableStandings.getBorderWidth();
				if (preferredSize.y > area.height) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = tableStandings.getVerticalBar().getSize();
					width -= vBarSize.x;
				}
				
				width -= colRank.getWidth();
				width -= colPoints.getWidth();
				width -= colOppMatch.getWidth();
				width -= colPLGame.getWidth();
				width -= colOppGame.getWidth();
				width -= colMatches.getWidth();
				
				if (width > 0) {
					colName.setWidth(width);
				}
			}
		});
	}

	private Menu createStandingsMenu() {
		Menu popUpMenu = new Menu(sShell, SWT.POP_UP);

		/**
		 * Adds a listener to handle enabling and disabling some items in the submenu.
		 */
		popUpMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu menu = (Menu) e.widget;
				MenuItem[] items = menu.getItems();

				items[0].setEnabled(controller.getCurrentTournament().isRoundFinished()); // print
			}
		});

		//Print standings

		MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Print standings");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				controller.printCurrentStandings(sShell);
			}
		});

		new MenuItem(popUpMenu, SWT.SEPARATOR);

		return popUpMenu;
	}

	private void seatingItems(TableItem ti, final Seating s) {
		String[] values = new String[8];

		values[0] = Integer.toString(s.getTable());
		if (s.getPlayer1() != null) {
			values[1] = s.getPlayer1().getName();
			values[2] = Integer.toString(s.getPlayer1PreviousMatchPoints());
		}
		if (s.getPlayer2() != null) {
			values[4] = s.getPlayer2().getName();
			values[5] = Integer.toString(s.getPlayer2PreviousMatchPoints());
		}
		else
		{
			values[4] = "* BYE *";			
		}
		
		if (s.isFinished()) {
			ti.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
			values[7] = s.getResult().toString();
		}
		else
		{
			ti.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			values[7] = "";
		}

		ti.setText(values);

		if(ti.getData() != s)
		{
			ti.setData(s);
	
		    TableEditor editor = new TableEditor (tableSeatings);
		    final Button button1 = new Button (tableSeatings, SWT.CHECK);
		    button1.setSelection(s.getPlayer1().hasDropped());
		    button1.pack ();
		    button1.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					controller.getCurrentTournament().dropPlayer(s.getPlayer1(), button1.getSelection() );
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					controller.getCurrentTournament().dropPlayer(s.getPlayer1(), button1.getSelection());
				}
		    	
		    });
		    editor.minimumWidth = button1.getSize ().x;    
		    editor.horizontalAlignment = SWT.CENTER;
		    editor.setEditor (button1, ti, 3);
		    ti.setData("TEDIT1", editor);

		    if(s.getPlayer2() != null)
		    {
			    editor = new TableEditor (tableSeatings);
			    final Button button2 = new Button (tableSeatings, SWT.CHECK);
			    button2.setSelection(s.getPlayer2().hasDropped());
			    button2.pack ();
			    button2.addSelectionListener(new SelectionListener() {
	
					public void widgetSelected(SelectionEvent e) {
						controller.getCurrentTournament().dropPlayer(s.getPlayer2(), button2.getSelection() );
					}
	
					public void widgetDefaultSelected(SelectionEvent e) {
						controller.getCurrentTournament().dropPlayer(s.getPlayer2(), button2.getSelection() );
					}
			    	
			    });
			    editor.minimumWidth = button2.getSize ().x;    
			    editor.horizontalAlignment = SWT.CENTER;
			    editor.setEditor (button2, ti, 6);
			    ti.setData("TEDIT2", editor);
		    }
		}
	}

	private void updateSeatings(Tournament tournament, Object hint) {
		if (hint instanceof Player) {
			Player p = (Player) hint;
			
			TableItem[] oldItems = tableSeatings.getItems();
			for (int i = 0; i < oldItems.length; i++) {
				Seating s = (Seating) oldItems[i].getData();
				
				if( s.contains(p))
				{
					seatingItems(oldItems[i], s);
					break;
				}
			}
		} else if (hint instanceof Seating) {
			int i;
			Seating s = (Seating) hint;
			TableItem[] oldItems = tableSeatings.getItems();
			for (i = 0; i < oldItems.length; i++) {
				if (oldItems[i].getData() == s) {
					break;
				}
			}

			TableItem ti = oldItems[i]; 
			if( i <oldItems.length && i != s.getTable() - 1)
			{
				cleanUpSeatingTable(ti);

				ti = new TableItem(tableSeatings, SWT.NULL, s.getTable() - 1);
			}
			seatingItems(ti, s);

		} else if (hint instanceof Round || hint instanceof Tournament) {
			TableItem[] oldItems = tableSeatings.getItems();
			for (int i = 0; i < oldItems.length; i++) {
				cleanUpSeatingTable( oldItems[i] );
			}

			if (hint instanceof Round) {
				Round r = (Round) hint;
				
				Object[] objects = { new Integer(r.getRound()) };
				String title = MessageFormat.format("Round #{0}", objects );
				roundTitle.setText(title);
				
				Iterator it = r.getSeatings().iterator();

				while (it.hasNext()) {
					Seating s = (Seating) it.next();

					TableItem ti = new TableItem(tableSeatings, SWT.NULL, s.getTable() - 1);
					seatingItems(ti, s);
				}
			}
		} else if (hint instanceof HintElapsedRoundTime) {
			HintElapsedRoundTime e = (HintElapsedRoundTime)hint;
			// round timer is reporting in			
			roundTimer.setText(e.formatElapsedTime());
			
			if( e.getMinutes() < 45)
				roundTimer.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
			else if( e.getMinutes() < 50)
				roundTimer.setBackground( colorOrange );
			else
				roundTimer.setBackground( colorRed );
		}
	}

	private void cleanUpSeatingTable(TableItem ti) {
		if(ti == null)
			return;
		
		TableEditor ed2 = (TableEditor) ti.getData("TEDIT2");
		
		if(ed2 != null)
		{
			ed2.getEditor().dispose();
			ed2.dispose();
		}
		TableEditor ed1 = (TableEditor) ti.getData("TEDIT1");
		if(ed1 != null)
		{
			ed1.getEditor().dispose();
			ed1.dispose();
		}
		ti.dispose();
	}

	private Menu createSeatingsMenu() {
		Menu popUpMenu = new Menu(sShell, SWT.POP_UP);

		/**
		 * Adds a listener to handle enabling and disabling some items in the
		 * Edit submenu.
		 */
		popUpMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				Menu menu = (Menu) e.widget;
				MenuItem[] items = menu.getItems();

				items[0].setEnabled(controller.getCurrentTournament().isRoundStartable() ); // seatings
				items[3].setEnabled(!controller.getCurrentTournament().isRoundFinished() && !controller.isRoundClockRunning()); // start clock
				items[4].setEnabled(controller.isRoundClockRunning()); // stop clock
				items[6].setEnabled(false); // Delete result
				
				if( tableSeatings.getSelectionCount() == 1)
				{
					Seating s = (Seating) tableSeatings.getSelection()[0].getData();
					
					items[6].setEnabled( s.isFinished() ); // seatings
				}

				items[8].setEnabled(controller.getCurrentTournament().hasStarted()); // Print seatings
				items[9].setEnabled(controller.getCurrentTournament().hasStarted()); // Print seatings

				// TODO Re-seat only possible if we have not recorded any results yet! && have a round available
			}
		});

		//Next round

		MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Seatings next round");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				controller.nextRound(sShell);
			}
		});

		//reseat

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Re-seat players");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
// TODO				controller.reseat(sShell);
			}
		});

		//reseat

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Manual seating");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
// TODO				controller.manualSeating(sShell);
			}
		});

		//Start round clock

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Start round clock");
		final Application app = this;
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				controller.startRound(sShell, app);
			}
		});

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Stop round clock");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				controller.stopRound(sShell, app);
			}
		});

		new MenuItem(popUpMenu, SWT.SEPARATOR);

		// Remove result

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Delete result");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = tableSeatings.getSelection();
				if (items.length == 0)
					return;

				Seating s = (Seating) items[0].getData();
				controller.getCurrentTournament().updateMatch(s, null);
			}
		});

		new MenuItem(popUpMenu, SWT.SEPARATOR);

		//Print seatings

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Print seatings");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				controller.printSeatings(sShell);
			}
		});

		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Print result slips");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				controller.printResultSlips(sShell);
			}
		});

		return popUpMenu;
	}

	private void createSeatingsTable(Composite parent) {
		tableSeatings = new Table(parent, SWT.FULL_SELECTION
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);

		GridData gid = new GridData();
		gid.horizontalAlignment = GridData.FILL;
		gid.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gid.horizontalSpan = 2;
		gid.grabExcessVerticalSpace = true;
		tableSeatings.setLayoutData(gid);

		tableSeatings.setHeaderVisible(true);
		tableSeatings.setLinesVisible(true);

		final TableColumn colTable = new TableColumn(tableSeatings, SWT.CENTER);
		colTable.setText("Table");
		colTable.setWidth(50);

		final TableColumn colPlayer1 = new TableColumn(tableSeatings, SWT.LEFT);
		colPlayer1.setText("Player1");

		final TableColumn colP1Points = new TableColumn(tableSeatings, SWT.RIGHT);
		colP1Points.setText("P1 Pts");
		colP1Points.setWidth(50);

		final TableColumn colP1Drop = new TableColumn(tableSeatings, SWT.CENTER);
		colP1Drop.setText("Drop");
		colP1Drop.setWidth(40);

		final TableColumn colPlayer2 = new TableColumn(tableSeatings, SWT.LEFT);
		colPlayer2.setText("Player2");

		final TableColumn colP2Points = new TableColumn(tableSeatings, SWT.RIGHT);
		colP2Points.setText("P2 Pts");
		colP2Points.setWidth(50);

		final TableColumn colP2Drop = new TableColumn(tableSeatings, SWT.CENTER);
		colP2Drop.setText("Drop");
		colP2Drop.setWidth(40);

		final TableColumn colResult = new TableColumn(tableSeatings, SWT.CENTER);
		colResult.setText("Result");
		colResult.setWidth(50);

		// TODO add seatings ID for secure entry
		
		tableSeatings.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = tableSeatings.getClientArea();
				Point preferredSize = tableSeatings.computeSize(SWT.DEFAULT,
						SWT.DEFAULT);
				int width = area.width - 2 * tableSeatings.getBorderWidth();
				if (preferredSize.y > area.height) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = tableSeatings.getVerticalBar().getSize();
					width -= vBarSize.x;
				}

				width -= colTable.getWidth();
				width -= colP1Points.getWidth();
				width -= colP1Drop.getWidth();
				width -= colP2Drop.getWidth();
				width -= colP2Points.getWidth();
				width -= colResult.getWidth();

				if (width > 0) {
					colPlayer1.setWidth(width / 2);
					colPlayer2.setWidth(width - colPlayer1.getWidth());
				}
			}
		});

		tableSeatings.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				tableSeatings.deselectAll();
			}
		});

		tableSeatings.setMenu(createSeatingsMenu());

		final TableEditor editor = new TableEditor(tableSeatings);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		tableSeatings.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				final TableItem ti = (TableItem) e.item;
				final Seating s = (Seating) ti.getData();
				
				// only edit non-finished pairings
				if(s.isFinished())
					return;
				
				String[] results = controller.getPrintableTournamentResults();

				final CCombo combo = new CCombo(tableSeatings, SWT.FLAT|SWT.READ_ONLY);
				combo.setVisibleItemCount(8);
				combo.setItems(results);
				
				Listener listener = new Listener() {
					public void handleEvent(final Event e) {
						if(e.detail == SWT.TRAVERSE_ESCAPE)
							e.doit = false;

						if (e.doit) {
							int index = combo.getSelectionIndex();

							if (index >= 0) {
								Result result = controller.getResult(index);

								controller.getCurrentTournament().updateMatch(s, result);
							}
						}

						combo.dispose();
					}
				};
				combo.addListener(SWT.FocusOut, listener);
				combo.addListener(SWT.Traverse, listener);
//				combo.addListener(SWT.Deactivate, listener);

				editor.setEditor(combo, ti, 7);
				combo.setFocus();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});
	}

	 private void saveWindowBounds() {
		PreferenceStore p = PreferenceLoader.getPreferenceStore();

		PreferenceConverter.setValue(p, "windowBounds", sShell.getBounds());
		p.setValue("windowMaximized", sShell.getMaximized());
	}
	 
	public void widgetDisposed(DisposeEvent e) {
	    saveWindowBounds();

	    controller.getCurrentTournament().deleteObserver(this);

		colorOrange.dispose();
		colorRed.dispose();
		
	    PreferenceLoader.saveStore();
	    PreferenceLoader.cleanUp();
	}
}