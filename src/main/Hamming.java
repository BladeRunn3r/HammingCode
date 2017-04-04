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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Hamming {
	
	@FXML private TextField entryBits;
	@FXML private TextField errorBitPosition;
	@FXML private Label generatedCode;
	@FXML private Label correctedCode;
	@FXML private Label errorCode;
	@FXML private Label entryMessageLabel;
	@FXML private Label errorMessageLabel;
	@FXML private Button generateCodeButton;
	@FXML private Button correctCodeButton;
	@FXML private Button pdfOutputButton;
	
	int a[], b[];
	FileChooser fileChooser = new FileChooser();
		
	private void initialize() {

		a = new int[entryBits.getText().length()];
				
	    for (int i = 0; i < entryBits.getText().length(); i++) {
	        a[entryBits.getText().length() - i - 1] = entryBits.getText().charAt(i) - '0';
	    }		
		b = Utils.generateCode(a);
	}
	
	@FXML public void doHamming() {
			
		if (entryBits.getText().matches("[0-1]+") && entryBits.getText() != "") {
			Platform.runLater(new Runnable() {
				@Override public void run() {	
					String generated = "";
					initialize();
		    
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
				}});
		}
		else {
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
	}
	
	@FXML public void fixError() {
			// Difference in the sizes of original and new array gives the number of parity bits added.

		Platform.runLater(new Runnable() {
			@Override public void run() {	
				
				if(errorBitPosition.getText().matches("[0-9]+") && errorBitPosition.getText() != "") {
					
					int errorBit = Integer.parseInt(errorBitPosition.getText());
						
					if((errorBit != 0) && (Integer.parseInt(errorBitPosition.getText()) <= generatedCode.getText().length())
							) {
							
						pdfOutputButton.setDisable(false);
						System.out.println("Enter position of a bit to alter to check for error detection at the receiver end (0 for no error):");
						String error = "";
					
						initialize();
					
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
		try {
			File file = fileChooser.showOpenDialog(MainApp.primaryStage);
			if (file != null) {
				Scanner scanner = new Scanner(file);
				entryMessageLabel.setText("");
				errorCode.setText("");
				correctedCode.setText("");
				errorMessageLabel.setText("");
				entryBits.setText(scanner.nextLine());
				doHamming();
				errorBitPosition.setText(scanner.nextLine());
				fixError();
				scanner.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@FXML public void randomValuesButton() {
		Random generator = new Random();
		String randomEntryBits = Integer.toBinaryString(generator.nextInt(2000) + 100);
		entryBits.setText(randomEntryBits);
		doHamming();
		String randomErrorBit = Integer.toString(generator.nextInt(randomEntryBits.length()) + 1);
		errorBitPosition.setText(randomErrorBit);
		fixError();
	}
	
	@FXML public void createPDF() {
        Document document = new Document();
        
        try {
			PdfWriter.getInstance(document, new FileOutputStream("output.pdf"));
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
        document.open();
        
        try {
            BaseFont baseFont = BaseFont.createFont("fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font newFont = new Font(baseFont, 12);
			document.add(new Paragraph("Wygenerowany kod: " + generatedCode.getText()));
			document.add(new Paragraph("Kod z błędem: " + errorCode.getText(), newFont));
			document.add(new Paragraph("Poprawiony kod: " + correctedCode.getText()));
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
        document.close();
	}
}