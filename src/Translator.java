
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import com.opencsv.CSVWriter;
import javax.swing.JRadioButton;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.Label;
import javax.swing.JTextField;

public class Translator extends JFrame  {

	private WebDriver driver =null;
	private URL resource;
	private JPanel contentPane;
	private JComboBox comboBox ;
	private JTextPane txtpnStatus;
	private JCheckBox chckbxOsDetection, chckbxBrowserAvailability,chckbxGeckoDriver,chckbxSizeOfSentence,chckbxSelectRows;
	private JRadioButton rdbtnChrome,rdbtnMozilla ;
	private JButton btnCheckSetting, btnExtract,btnSelectFilecsv, btnTranslate,btnStop,btnCheckUpdate;
	public JProgressBar progressBar;
	private static String status="status: ";
	private static String OS  = System.getProperty("os.name").toLowerCase();
	private static int bit=Integer.valueOf(System.getProperty("sun.arch.data.model"));
	private static double version=1.0;
	public  int os,browser,max=0,lines = 0,exLine=0,inc;
	private openFile of;
	private WebElement giventext;
	private CSVWriter writer=null;
	private boolean err=false;
	private boolean exceed =false;
	private boolean exit=false;
	private List<String> records = new ArrayList<>();
	private List<String> translated = new ArrayList<>();
	private JLabel label ;
	private String DIR =System.getProperty("user.home")+"/result.csv";
	private JLabel lblOutput;
	private JTextField textField;
	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Translator frame = new Translator();
					frame.setVisible(true);
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public static boolean isUnix() {

		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
		
	}
	public static boolean isWindows() {

		return (OS.indexOf("win") >= 0);

	}
	/**
	 * Create the frame.
	 */
	public Translator() {
		setTitle("English to Bangla translator Dataset v"+String.valueOf(version));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 532, 510);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		rdbtnChrome = new JRadioButton("Chrome");
		rdbtnChrome.setBounds(8, 8, 91, 23);
		contentPane.add(rdbtnChrome);
		
	

		
		rdbtnChrome.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				rdbtnMozilla.setSelected(false);
				btnCheckSetting.setEnabled(true);
				browser=1;
				btnCheckUpdate.setEnabled(false);

			}
		});
		
		rdbtnMozilla = new JRadioButton("Mozilla");
		rdbtnMozilla.setBounds(8, 35, 91, 23);
		contentPane.add(rdbtnMozilla);
		rdbtnMozilla.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				rdbtnChrome.setSelected(false);
				btnCheckSetting.setEnabled(true);
				browser=2;
				btnCheckUpdate.setEnabled(false);
			}
		});
		
		progressBar = new JProgressBar();
		progressBar.setBounds(156, 17, 148, 14);
		contentPane.add(progressBar);
		progressBar.setVisible(false);
		//progressBar.setMaximum(100);
		//progressBar.setMinimum(0);
		
		btnCheckSetting = new JButton("Check");
		btnCheckSetting.setBounds(22, 435, 163, 25);
		contentPane.add(btnCheckSetting);
		btnCheckSetting.setEnabled(false);
		btnCheckSetting.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				progressBar.setIndeterminate(true);
				progressBar.setVisible(true);
				
				txtpnStatus.setForeground(Color.BLUE);
				txtpnStatus.setText(status);
				status=status+"\n"+"Detecting OS...";
				//txtpnStatus.setForeground(Color.GREEN);
				txtpnStatus.setText(status);
				checkOS();
				txtpnStatus.setText(status);
				if(err) {
					status=status+"\n"+"Further examination is halted!!";
					txtpnStatus.setText(status);
				}
				else {

					status=status+"\n"+"Detecting Browser...";
					checkbrowser();
					if(err) {
						status=status+"\n"+"The browser is not installed.\nInstall the browser and try again";
						txtpnStatus.setText(status);
					}else {
						chckbxBrowserAvailability.setSelected(true);
						status=status+"\n"+"Please select the browser version and click Extract";
						txtpnStatus.setText(status);
						btnExtract.setEnabled(true);
						rdbtnMozilla.setEnabled(false);
						rdbtnChrome.setEnabled(false);
						comboBox.setEnabled(true);
						comboBox.removeAllItems();
						if(browser==1) {
							comboBox.addItem("Chrome v73");
							comboBox.addItem("Chrome v74");
							comboBox.addItem("Chrome v75");
						}else {
							comboBox.addItem("Mozilla 32 bit");
							if(bit==64)
								comboBox.addItem("Mozilla 64 bit");
						}
						progressBar.setIndeterminate(false);
						progressBar.setVisible(false);
						btnExtract.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent arg0) {
								// TODO Auto-generated method stub
								progressBar.setIndeterminate(true);
								progressBar.setVisible(true);
								status=status+"\nExtracting .... ";
								txtpnStatus.setText(status);
								String x = String.valueOf(comboBox.getSelectedItem());
								String dir=System.getProperty("user.home");
								if(x.equalsIgnoreCase("Chrome v73")) {
									if(os==1) {
										Translator main = new Translator();
								        File file;
										file = main.getFileFromResources("chromedriver73.exe");
										fileplace(file,dir+"\\chromedriver.exe");

									}else {
										Translator main = new Translator();
								        File file;
										file = main.getFileFromResourcesL("chromedriver73");
										fileplace(file,dir+"/chromedriver");
										setFileExecutable(dir+"/chromedriver");

									}
								}else if(x.equalsIgnoreCase("Chrome v74")) {
									if(os==1) {
										Translator main = new Translator();
								        File file;
										file = main.getFileFromResources("chromedriver74.exe");
										  fileplace(file,dir+"\\chromedriver.exe");
								      
									}else {
										Translator main = new Translator();
								        File file;
										file = main.getFileFromResourcesL("chromedriver74");
										fileplace(file,dir+"/chromedriver");
										setFileExecutable(dir+"/chromedriver");

									}
								}else if(x.equalsIgnoreCase("Chrome v75")) {
									if(os==1) {
										Translator main = new Translator();
								        File file;
										file = main.getFileFromResources("chromedriver75.exe");
										fileplace(file,dir+"\\chromedriver.exe");
								        
									}else {
										Translator main = new Translator();
								        File file;
										file = main.getFileFromResourcesL("chromedriver75");
										fileplace(file,dir+"/chromedriver");
										setFileExecutable(dir+"/chromedriver");

									}
								}else if(x.equalsIgnoreCase("Mozilla 32 bit")){ 
									if(os==1) {
										Translator main = new Translator();
								        File file;
										file = main.getFileFromResources("geckodriver32.exe");
										 fileplace(file,dir+"\\geckodriver.exe");
								       
									}else {
										Translator main = new Translator();
								        File file;
										file = main.getFileFromResourcesL("geckodriver32");
										fileplace(file,dir+"/geckodriver");
										setFileExecutable(dir+"/geckodriver");

									}
								}else {
									if(os==1) {
										Translator main = new Translator();
								        File file;
										file = main.getFileFromResources("geckodriver64.exe");
										fileplace(file,dir+"\\geckodriver.exe");
								        
									}else {
										Translator main = new Translator();
								        File file;
										file = main.getFileFromResourcesL("geckodriver64");

										fileplace(file,dir+"/geckodriver");
										setFileExecutable(dir+"/geckodriver");
								        	

									}
								}
								progressBar.setIndeterminate(false);
								progressBar.setVisible(false);
								if(!err) {
									
									status=status+"done";
									txtpnStatus.setText(status);
									chckbxGeckoDriver.setSelected(true);
									btnSelectFilecsv.setEnabled(true);
									btnCheckSetting.setText("Check file");
									btnCheckSetting.setEnabled(false);
								}
								else {
									status=status+"failed!";
									txtpnStatus.setText(status);
								}
							}

							private void setFileExecutable(String dir) {
								// TODO Auto-generated method stub
							    File file = new File(dir); 
							    file.setExecutable(true);
							}

							private void fileplace(File file, String dir) {
								// TODO Auto-generated method stub
						        byte[] buffer = new byte[1024];
						        try {
									InputStream bis = new  FileInputStream(file);
									try {
										File tmpFile =new File(dir);
										tmpFile.delete();
									}catch (Exception e){
										e.printStackTrace();
									}
									OutputStream os =new FileOutputStream (dir);
									int length;
									 
									 while ((length = bis.read(buffer)) > 0) {
								            os.write(buffer, 0, length);
								     }
									 bis.close();
									 os.close();
								} catch (FileNotFoundException e) {
									err=true;
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						});
						
						btnSelectFilecsv.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								// TODO Auto-genferated method stub
								of =new openFile();
								of.pickMe();
								label.setText("Input: "+of.selectedFile.getAbsolutePath());
								try (BufferedReader br = new BufferedReader(new FileReader(of.selectedFile.getAbsolutePath()))) {
								    String line=new String();		
								    int range=0;
								    if(chckbxSelectRows.isSelected()) {
								    	range=Integer.valueOf(textField.getText());
								    }
								    while ((line = br.readLine()) != null) {
								    	lines++;
								    	
								    	if(lines==1)
								    			continue;
								    	records.add(line);
								    	if(max< line.length() ){
								    		max=line.length();
								    		exLine=lines;
								    	}
								    	if(lines==range && chckbxSelectRows.isSelected())
								    			break;
								    	//System.out.println(line);
								    }
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									err=true;
								}
								if(max>5000) {
									err=true;
									status=status+"\nThe size of the sentences is Exceeded at "+String.valueOf(exLine);
									txtpnStatus.setText(status);
								}else if(err) {
									status=status+"\nSomething is wrong";
									txtpnStatus.setText(status);
								}
								else {
									chckbxSizeOfSentence.setSelected(true);
									btnTranslate.setEnabled(true);
								}
								txtpnStatus.setText(status);
							}
						});

					}
				}
			}

			private void checkbrowser() {
				// TODO Auto-generated method stub
				if (os==1) {
					if(browser==1) {
						File chrome = new File("C:/Program Files (x86)/Google/Chrome");
						if(chrome.exists()) {
							status=status+"chrome(ok)";
							txtpnStatus.setText(status);
						}else {
							status=status+"no";
							txtpnStatus.setText(status);
							err=true;
						}
					}else {
						File mozilla = new File("C:/Program Files/Mozilla Firefox");
						if(mozilla.exists()) {
							status=status+"mozilla(ok)";
							txtpnStatus.setText(status);
						}
						else {
							status=status+"no";
							txtpnStatus.setText(status);
							err=true;
						}
					}
				}else {
					if(browser==1) {
						
						File chrome = new File("/usr/bin/chromium-browser");
						if(chrome.exists()) {
							status=status+"chromium(ok)";
							txtpnStatus.setText(status);
						}else {
							status=status+"no";
							txtpnStatus.setText(status);
							err=true;
						}
					}else {
						File mozilla = new File("/usr/lib/firefox/firefox");
						if(mozilla.exists()) {
							status=status+"mozilla(ok)";
							txtpnStatus.setText(status);
						}
						else {
							status=status+"no";
							txtpnStatus.setText(status);
							err=true;
						}
					}
				}
			}

			private void checkOS() {
				// TODO Auto-generated method stub
				if (isWindows()) {
					os=1;
					chckbxOsDetection.setSelected(true);
					status=status+"Windows(ok)";
				}else if (isUnix()) {
					os=2;
					chckbxOsDetection.setSelected(true);
					status=status+"Linux(ok)";
				}else {
					os=0;
					status=status+"No"+"\n"+"Your OS cant be identified.";
					txtpnStatus.setText(status);
					status=status+"No";
					err=true;
				}
			}


		});
		
		
		btnTranslate = new JButton("Translate");
		btnTranslate.setEnabled(false);
		btnTranslate.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				progressBar.setVisible(true);
				progressBar.setForeground(Color.ORANGE);
				progressBar.setMaximum(0);
				progressBar.setMaximum(100);
				progressBar.setStringPainted(true);

				Task task = new Task();
				task.addPropertyChangeListener(new PropertyChangeListener() {
						
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
							// TODO Auto-generated method stub
						if ("progress".equals(evt.getPropertyName())) {
							//System.out.println("yess");
					        progressBar.setValue((Integer)evt.getNewValue()*100/(lines-1));
					    }
					}
				});
				task.execute();

				btnStop.setEnabled(true);
			}
		});
		btnTranslate.setBounds(217, 435, 114, 25);
		contentPane.add(btnTranslate);
		
		btnStop = new JButton("Finish");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnStop.setBounds(365, 435, 114, 25);
		contentPane.add(btnStop);
		btnStop.setEnabled(false);
		 
		btnSelectFilecsv = new JButton("Select File(.csv)");
		btnSelectFilecsv.setBounds(288, 61, 163, 25);
		contentPane.add(btnSelectFilecsv);
		btnSelectFilecsv.setEnabled(false);
		
		btnExtract = new JButton("Extract");
		btnExtract.setBounds(27, 351, 114, 25);
		contentPane.add(btnExtract);
		btnExtract.setEnabled(false);
		
		chckbxSizeOfSentence = new JCheckBox("Size of sentence");
		chckbxSizeOfSentence.setBounds(23, 171, 163, 23);
		contentPane.add(chckbxSizeOfSentence);
		chckbxSizeOfSentence.setEnabled(false);
		
		chckbxGeckoDriver = new JCheckBox("Gecko Driver");
		chckbxGeckoDriver.setBounds(23, 193, 126, 23);
		contentPane.add(chckbxGeckoDriver);
		chckbxGeckoDriver.setEnabled(false);
		
		chckbxBrowserAvailability = new JCheckBox("Browser Availability");
		chckbxBrowserAvailability.setBounds(23, 220, 163, 23);
		contentPane.add(chckbxBrowserAvailability);
		chckbxBrowserAvailability.setEnabled(false);
		
		chckbxOsDetection = new JCheckBox("OS Detection");
		chckbxOsDetection.setBounds(23, 243, 126, 23);
		contentPane.add(chckbxOsDetection);
		chckbxOsDetection.setEnabled(false);
		
		txtpnStatus = new JTextPane();
		txtpnStatus.setText("Status :");
		txtpnStatus.setBounds(190, 123, 315, 284);
		contentPane.add(txtpnStatus);
		
		
		comboBox = new JComboBox();
		comboBox.setBounds(22, 315, 127, 24);
		contentPane.add(comboBox);
		comboBox.setEnabled(false);
		
		JLabel lblChromeVersion = new JLabel("version");
		lblChromeVersion.setBounds(22, 291, 116, 23);
		contentPane.add(lblChromeVersion);
		
		label = new JLabel("Input: No file");
		label.setBounds(23, 66, 216, 15);
		contentPane.add(label);
		
		lblOutput = new JLabel("Output :"+DIR);
		lblOutput.setBounds(22, 93, 457, 15);
		contentPane.add(lblOutput);
		
		btnCheckUpdate = new JButton("Check Update");
		btnCheckUpdate.setBounds(365, 7, 140, 25);
		contentPane.add(btnCheckUpdate);
		
		JLabel lblSelectRows = new JLabel("Select Rows");
		lblSelectRows.setBounds(22, 145, 102, 15);
		contentPane.add(lblSelectRows);
		
		chckbxSelectRows = new JCheckBox("Select Rows");
		chckbxSelectRows.setBounds(23, 114, 126, 23);
		contentPane.add(chckbxSelectRows);
		chckbxSelectRows.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(chckbxSelectRows.isSelected()) {
					textField.setEnabled(true);
				}else {
					textField.setEnabled(false);
				}
			}
		});
		
		textField = new JTextField();
		textField.setBounds(110, 144, 75, 19);
		contentPane.add(textField);
		textField.setColumns(10);
		textField.setEnabled(false);
		btnCheckUpdate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				progressBar.setVisible(true);
				progressBar.setIndeterminate(true);
				status="Locate the Application file...";
				txtpnStatus.setText(status);
				status="ok\nUpdating...\nListing files to update...";
				txtpnStatus.setText(status);
				updateJar uj =new updateJar();
				
				openFile of =new openFile();
				of.locateJar();
				File srcJarFile  = of.selectedFile;
				List<String> filesList = new ArrayList<>();
				try {
					filesList=ListFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block

					status=status+"failed";
					txtpnStatus.setText(status);
					e1.printStackTrace();
				}
				status=status+"ok\nDownloading updated files...";
				txtpnStatus.setText(status);
				try {
					if(version!=Double.valueOf(filesList.get(0))) {
					uj.updateJarFile(srcJarFile,filesList);
					version=Double.valueOf(filesList.get(0));
					}else {
						status=status+"success\nAlready up-to-date. version "+String.valueOf(version)+"\n";
						txtpnStatus.setText(status);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					status="failed installing";
					txtpnStatus.setText(status);
					e.printStackTrace();
				}

				status=status+"success\n";
				txtpnStatus.setText(status);
				progressBar.setIndeterminate(false);
				progressBar.setVisible(false);
			}

			private List<String> ListFile() throws IOException {
				// TODO Auto-generated method stub
				List<String> filesList = new ArrayList<>();
				String fromFile = "https://trackingdaily.000webhostapp.com/file_list.txt";
			    String toFile = System.getProperty("user.home")+"/tmp.txt" ;
				URL website = new URL(fromFile);
				ReadableByteChannel rbc = Channels.newChannel(website.openStream());
				FileOutputStream fos = new FileOutputStream(toFile);
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				fos.close();
				rbc.close();
				
				File file = new File(toFile); 
				
				BufferedReader br = new BufferedReader(new FileReader(file)); 
				  
				String st; 
				while ((st = br.readLine()) != null) {
				    filesList.add(st); 
				} 

				file.delete();
				return filesList;
			}
		});
	}
	private File getFileFromResources(String fileName)  {
		
		// TODO Auto-generated method stub
		 ClassLoader classLoader = getClass().getClassLoader();
				 InputStream is=classLoader.getResourceAsStream(fileName);
				 try {
					FileOutputStream outputStream = new FileOutputStream(System.getProperty("user.home")+"\\temp.exe");
					int read = 0;
					byte[] bytes = new byte[1024];
			        while ((read = is.read(bytes)) != -1) {
			            outputStream.write(bytes, 0, read);
			        }
			        outputStream.close();
			        
		
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 return new File(System.getProperty("user.home")+"\\temp.exe");
	}

	 private File getFileFromResourcesL(String fileName) {
		 ClassLoader classLoader = getClass().getClassLoader();
		 resource =classLoader.getResource(fileName);
	     
		 if (resource == null) {
	           throw new IllegalArgumentException("file is not found!!!!");

	     } else { 
	           return new File(resource.getFile());
	     }
	 }
	    public class Task extends SwingWorker{
	    	
	        @Override 
	        protected Object doInBackground() throws Exception {
	        	int progress=0;
				Thread thread =new Thread (new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(browser==2 && os==2 ) {
							System.setProperty("webdriver.gecko.driver", System.getProperty("user.home")+"/geckodriver");
							driver =new FirefoxDriver() ;
						}else if(browser==2 && os==1) {
							System.setProperty("webdriver.gecko.driver", System.getProperty("user.home")+"/geckodriver.exe");
							driver =new FirefoxDriver() ;
						}else if(browser==1 && os==2) {
							System.setProperty("webdriver.chrome.driver",System.getProperty("user.home")+"/chromedriver");
							driver =new ChromeDriver() ;
						}else if(browser==1 && os==1) {
							System.setProperty("webdriver.chrome.driver",System.getProperty("user.home")+"/chromedriver.exe");
							driver =new ChromeDriver() ;
						}
				        String baseUrl = "https://translate.google.com/#view=home&op=translate&sl=en&tl=bn";					
				        driver.get(baseUrl);
						giventext =driver.findElement(By.xpath("//*[@id=\"source\"]"));
					}
				});
				
				
				thread.start();
				try {
					thread.join();
					
				} catch (InterruptedException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}

				//System.out.println(translated);
				File file = new File(System.getProperty("user.home")+"/result.csv");
				try {
					
					
					PrintWriter writer = new PrintWriter(file, "UTF-8");
					
					for (String string : records) {
						progress++;
						//System.out.println(i);
						setProgress(progress);
						giventext.sendKeys(string);
	
						try {
							TimeUnit.SECONDS.sleep(2);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("cant wait");
						}
						WebElement outtext =driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/div[1]/div[1]/div[2]/div[3]/div[1]/div[2]/div/span[1]/span"));
						String ans=outtext.getText();
						translated.add(ans);
						giventext.clear();
						writer.println(ans);
					}
					
					driver.close();
						
						
					writer.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            return null;
	        }
	    }
}
