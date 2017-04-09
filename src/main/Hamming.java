package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Hamming {
	
	@FXML private TextField entryBits;
	@FXML private TextField errorBitPosition;
	@FXML private TextField gatesEntryBits;
	@FXML private Label generatedCode;
	@FXML private Label correctedCode;
	@FXML private Label errorCode;
	@FXML private Label entryMessageLabel;
	@FXML private Label errorMessageLabel;
	@FXML private Button generateCodeButton;
	@FXML private Button correctCodeButton;
	@FXML private Button pdfOutputButton;
	@FXML private Button andGate;
	@FXML private Button orGate;
	@FXML private Button xorGate;
	@FXML private CheckBox randomErrorBitBox;
	
	int a[], b[];
	FileChooser fileChooser = new FileChooser();
	Random generator = new Random();
	String input, randomBits;
	 private static boolean usingFileChooser = false;
		
	private void initializeHamming() {

		a = new int[entryBits.getText().length()];
				
	    for (int i = 0; i < entryBits.getText().length(); i++) {
	        a[entryBits.getText().length() - i - 1] = entryBits.getText().charAt(i) - '0';
	    }		
		b = Utils.generateCode(a);
	}
	
	private void initializeGates() {
				input = gatesEntryBits.getText();
				StringBuilder generated = new StringBuilder(30);
				
				for (int i = 0; i < gatesEntryBits.getText().length(); i++) {
					int bit = (generator.nextBoolean()) ? 1 : 0;
					generated.append(bit);
				}			
				randomBits = generated.toString();
	}
	
	private void resetGUI() {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				entryMessageLabel.setTextAlignment(TextAlignment.CENTER);
				entryMessageLabel.setText("nie wprowadzono danych lub wprowadzono wartości różne od bitów");
				entryMessageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14pt;");
				errorCode.setText("");
				correctedCode.setText("");
				generatedCode.setText("");
				errorBitPosition.setText("");
				errorMessageLabel.setText("");
				pdfOutputButton.setDisable(true);
				correctCodeButton.setDisable(true);
			}
		});
	}
	
	private void handleRandomingCheckBox() {
		if (randomErrorBitBox.isSelected()) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					String randomErrorBit = Integer.toString(generator.nextInt(entryBits.getText().length()) + 1);
					errorBitPosition.setText(randomErrorBit);
					fixError();
				}				
			});
		}
		else { 
			errorBitPosition.setText("");
			pdfOutputButton.setDisable(true);
		}
	}
	
	@FXML public void doHamming() {
			
		if (entryBits.getText().matches("[0-1]+") && entryBits.getText() != "") {
			Platform.runLater(new Runnable() {
				@Override public void run() {	
					String generated = "";
					initializeHamming();
		    
					System.out.println("You entered:");
					for (int i = 0 ; i < entryBits.getText().length() ; i++) {
					System.out.print(a[entryBits.getText().length() - i - 1]);
					}
					System.out.println();
								
					System.out.println("Generated code is:");
					for (int i = 0 ; i < b.length ; i++) {
						System.out.print(b[b.length-i-1]);
						generated = generated + Integer.toString(b[b.length-i-1]);				
					}
					
					entryMessageLabel.setText("");
					errorMessageLabel.setText("");
					errorCode.setText("");
					correctedCode.setText("");
					generatedCode.setText(generated);
					correctCodeButton.setDisable(false);
					System.out.println();
					System.out.println(usingFileChooser);
					if (usingFileChooser == false) handleRandomingCheckBox();
				}

			});
		}
		else resetGUI();
	}
	
	@FXML public void fixError() {
			// Difference in the sizes of original and new array gives the number of parity bits added.

		Platform.runLater(new Runnable() {
			@Override public void run() {	
				
				if (errorBitPosition.getText().matches("[0-9]+") && (errorBitPosition.getText() != "")) {
					
					int errorBit = Integer.parseInt(errorBitPosition.getText());
						
					if ((errorBit != 0) && (Integer.parseInt(errorBitPosition.getText()) <= generatedCode.getText().length())) {
							
						pdfOutputButton.setDisable(false);
						System.out.println("Enter position of a bit to alter to check for error detection at the receiver end (0 for no error):");
						String error = "";
					
						initializeHamming();
					
						b[errorBit - 1] = (b[errorBit - 1] + 1)%2;
	
						System.out.println("Sent code is:");
						
						for(int i = 0 ; i < b.length ; i++) {
							System.out.print(b[b.length-i-1]);
							error = error + Integer.toString(b[b.length-i-1]);
						}
						
						errorCode.setText(error);
						System.out.println();
						String corrected = Utils.receive(b, b.length - a.length);
						correctedCode.setText(corrected);
						errorMessageLabel.setText("");
						usingFileChooser = false;
					}
								
					else {
						errorMessageLabel.setText("wprowadzono niepoprawną lokalizację");
						errorMessageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16pt;");
						errorCode.setText("");
						correctedCode.setText("");
						pdfOutputButton.setDisable(true);
					}
				}
				else {
					errorMessageLabel.setTextAlignment(TextAlignment.CENTER);
					errorMessageLabel.setText("nie wprowadzono danych lub wprowadzono wartości różne od liczb");
					errorMessageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14pt;");
					errorCode.setText("");
					correctedCode.setText("");
					pdfOutputButton.setDisable(true);
				}
			}
		});
	}
	
	@FXML public void fileChooserButton() {
		fileChooser.setTitle("Open .txt File");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
		File file = fileChooser.showOpenDialog(MainApp.primaryStage);
		
		if (file != null) {
			usingFileChooser = true;
			Scanner scanner;
			try {
				scanner = new Scanner(file);
				entryMessageLabel.setText("");
				errorCode.setText("");
				correctedCode.setText("");
				errorMessageLabel.setText("");
				entryBits.setText(scanner.nextLine().replaceAll("\\s",""));
				doHamming();
				Platform.runLater(new Runnable() {
					@Override public void run() {
						errorBitPosition.setText(scanner.nextLine());
						System.out.println(errorBitPosition.getText());
						scanner.close();
					}
				});
				fixError();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML public void randomValuesButton() {
		String randomEntryBits = Integer.toBinaryString(generator.nextInt(2000) + 100);
		entryBits.setText(randomEntryBits);
		doHamming();
		handleRandomingCheckBox();
	}
	
	@FXML public void createPDF() {
        Document document = new Document();
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("pdf Files", "*.pdf"));
        fileChooser.setTitle("Save pdf file");
        fileChooser.setInitialFileName("output.pdf");
        File savedFile = fileChooser.showSaveDialog(MainApp.primaryStage);
        
        if (savedFile != null) {
	        try {
				PdfWriter.getInstance(document, new FileOutputStream(savedFile));
			} catch (DocumentException | IOException e) {
				e.printStackTrace();
			}
	        document.open();
	        
	        try {
	            BaseFont baseFont = BaseFont.createFont("fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
	            Font newFont = new Font(baseFont, 12);
				document.add(new Paragraph("Wygenerowany kod: " + generatedCode.getText()));
				document.add(new Paragraph("Bit błędu: " + errorBitPosition.getText(), newFont));
				document.add(new Paragraph("Kod z błędem: " + errorCode.getText(), newFont));
				document.add(new Paragraph("Poprawiony kod: " + correctedCode.getText()));
			} catch (DocumentException | IOException e) {
				e.printStackTrace();
			}
	        document.close();
        }
	}
	
	@FXML public void doAndGate() {
		if (gatesEntryBits.getText().matches("[0-1]+") && gatesEntryBits.getText() != "") {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					initializeGates();
					int result = (Integer.parseInt(input, 2)) & (Integer.parseInt(randomBits, 2));
					entryBits.setText(Integer.toBinaryString(result));
					doHamming();
					handleRandomingCheckBox();
				}
			});
		}
		else resetGUI();
	}
			
	@FXML public void doOrGate() {
		if (gatesEntryBits.getText().matches("[0-1]+") && gatesEntryBits.getText() != "") {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					initializeGates();
					int result = (Integer.parseInt(input, 2)) | (Integer.parseInt(randomBits, 2));
					entryBits.setText(Integer.toBinaryString(result));
					doHamming();
					handleRandomingCheckBox();
				}
			});
		}
		else resetGUI();
	}
	
	@FXML public void doXorGate() {
		if (gatesEntryBits.getText().matches("[0-1]+") && gatesEntryBits.getText() != "") {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					initializeGates();
					int result = (Integer.parseInt(input, 2)) ^ (Integer.parseInt(randomBits, 2));
					entryBits.setText(Integer.toBinaryString(result));
					doHamming();
					handleRandomingCheckBox();
				}
			});
		}
		else resetGUI();	
	}
}