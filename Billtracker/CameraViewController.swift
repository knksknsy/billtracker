//
//  CameraViewController.swift
//  Billtracker
//
//  Created by Valdet D. on 10.12.17.
//  Copyright © 2017 Valdet D.. All rights reserved.
//

import UIKit
import AVFoundation
import Photos


class CameraViewController: UIViewController, UITextFieldDelegate,UIImagePickerControllerDelegate, UINavigationControllerDelegate, AVCapturePhotoCaptureDelegate {

    @IBOutlet weak var previewView: UIView!
    @IBOutlet weak var sumInput: UITextField!
    @IBOutlet weak var captureButton: UIBarButtonItem!
    @IBOutlet weak var savePhoto: UIBarButtonItem!
    //@IBOutlet weak var capturedPhoto: UIImageView!
    
    // verbindet alle weiteren Objekte, die zur Bildaufnahme,- anzeige und -speicherung erforderlich sind
    let captureSession = AVCaptureSession()
    var photoOut = AVCapturePhotoOutput()
    
    
    var videoInput: AVCaptureDeviceInput!
    var previewLayer: AVCaptureVideoPreviewLayer!
    //var useRearCamera = true
    
    
    
    
    // hier werden Fotos gespeichert
    //var photos = [UIImage]()
    
    

    override func viewDidLoad() {
        super.viewDidLoad()

        
        // Set the output on the capture session
        //captureSession.addOutput (photoOut)

        
        sumInput.delegate = self
        savePhoto.isEnabled = false
        
        // Kamera wählen
        let device: AVCaptureDevice!
        device = AVCaptureDevice.default(for: AVMediaType.video)
        
        if device == nil {
            print("Kein Zufgriff auf die Kamera")
            exit(EXIT_FAILURE)
        }
        
        //Session mit Kamera-Input verbinden
        videoInput = try? AVCaptureDeviceInput(device:device)
        if (captureSession.canAddInput(videoInput)){
            captureSession.addInput(videoInput)
        } else {
            print("Kann AVCaptureseseion nicht erreichen")
            exit(EXIT_FAILURE)
        }
        
        // Session mit Photo-Output verbinden
        if captureSession.canAddOutput(photoOut) {
            
            captureSession.addOutput(photoOut)
        } else {
            print("Kann Campture-Output nicht zur CaptureSesion hinzufügen")
            exit(EXIT_FAILURE)
        }
        
        
        // Zugriff auf PreviewLayer der Session

        previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        previewLayer.videoGravity = AVLayerVideoGravity.resizeAspectFill
        //videoPreviewLayer?.frame = view.layer.bounds
        previewView.frame = self.view.frame
        self.view.layer.insertSublayer(previewLayer, at: 1)
        //previewView.layer.addSublayer(previewLayer)
        
    }
    
    
    //
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        view.endEditing(true)
    }
    
    
    //
    override func viewDidAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        previewLayer.frame = previewView.bounds
        captureSession.startRunning()
        
    }
    

    
    @IBAction func takePhoto(_ sender: Any) {
        //let settings = AVCapturePhotoSettings()
        // capturePhoto führt die Aufnahme durch --> Ergebnis wird dann später in der Delegate-Methode photoOutput mit dem Param. didFinischProcessingPhoto

        // Make sure capturePhotoOutput is valid
        //guard let photoOut = self.photoOut else { return }
        
//
        let photoSettings : AVCapturePhotoSettings!
        photoSettings = AVCapturePhotoSettings.init(format: [AVVideoCodecKey: AVVideoCodecType.jpeg])
        photoSettings.isAutoStillImageStabilizationEnabled = true
        photoSettings.flashMode = .auto
        photoSettings.isHighResolutionPhotoEnabled = false

        self.photoOut.capturePhoto(with: photoSettings, delegate: self)
        //captureSession.sessionPreset = AVCaptureSession.Preset.photo
        
captureSession.stopRunning()
//
//        let alert = UIAlertController(title: "Information", message: "Bild wurde erfolgreich gespeichert", preferredStyle: .alert)
//        alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .`default`, handler: { _ in
//            NSLog("The \"OK\" alert occured.")
//        }))
//        self.present(alert, animated: true, completion: nil)
        


   }
    
//    @IBAction func savePhoto(_ sender: Any) {
        // hier die Funktion
        
//        
//        let photoSettings : AVCapturePhotoSettings!
//        photoSettings = AVCapturePhotoSettings.init(format: [AVVideoCodecKey: AVVideoCodecType.jpeg])
//        photoSettings.isAutoStillImageStabilizationEnabled = true
//        photoSettings.flashMode = .auto
//        photoSettings.isHighResolutionPhotoEnabled = false
//        
//        self.photoOut.capturePhoto(with: photoSettings, delegate: self)
        
//        let alert = UIAlertController(title: "Information", message: "Bild wurde erfolgreich gespeichert", preferredStyle: .alert)
//        alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .`default`, handler: { _ in
//            NSLog("The \"OK\" alert occured.")
//        }))
//        self.present(alert, animated: true, completion: nil)
        //captureSession.stopRunning()

//        imagePickerController = UIImagePickerController()
//
//        imagePickerController.delegate = self
//        imagePickerController.sourceType = .camera
//        imagePickerController.allowsEditing = true
//        present(imagePickerController, animated: true, completion: nil)
    
//    }
    
//    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
//        imagePickerController.dismiss(animated: true, completion: nil)
//        capturedPhoto.image = info[UIImagePickerControllerOriginalImage] as? UIImage
//    }
//
    
    
    
    // Für iOS 11
    
    func photoOutput(_ output: AVCapturePhotoOutput, didFinishProcessingPhoto photo: AVCapturePhoto, error: Error?) {
        PHPhotoLibrary.shared().performChanges( {
            let creationRequest = PHAssetCreationRequest.forAsset()
            creationRequest.addResource(with: PHAssetResourceType.photo, data: photo.fileDataRepresentation()!, options: nil)
        }, completionHandler: nil)
    }
    
    func photoOutput(_ captureOutput: AVCapturePhotoOutput,
                     didFinishCaptureFor resolvedSettings: AVCaptureResolvedPhotoSettings,
                     error: Error?) {
        
        guard error == nil else {
            print("Error in capture process: \(String(describing: error))")
            return
        }
    }
    

    
   //****** ab hier Textfeld
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        // User pressed the delete-key to remove a character, this is always valid, return true to allow change
//        if string.isEmpty { return true }
//
//
//        if sumInput.text == ""{
//            self.savePhoto.isEnabled = false
//        } else {
//            self.savePhoto.isEnabled = true
//        }
        
        //Return false if you don't want the textfield to be updated
        //return true
        
//
        let currentText = sumInput.text ?? ""

                if currentText == ""{
                    self.savePhoto.isEnabled = false
                } else {
                    self.savePhoto.isEnabled = true
                }

       // Return false if you don't want the textfield to be updated
        //return true
        
        
        let replacementText = (currentText as NSString).replacingCharacters(in: range, with: string)
        
        return replacementText.isValidDouble(maxDecimalPlaces: 2)
    }
    
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        sumInput.resignFirstResponder()
        return true
    }
    
    
    // Start Editing The Text Field
    func textFieldDidBeginEditing(_ textField: UITextField) {
        moveTextField(textField, moveDistance: -208, up: true)
    }
    
    // Finish Editing The Text Field
    func textFieldDidEndEditing(_ textField: UITextField) {
        moveTextField(textField, moveDistance: -208, up: false)
        
        
        
    }
    
    // Hide the keyboard when the return key pressed
//    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
//        textField.resignFirstResponder()
//        return true
//    }
    
    // Move the text field in a pretty animation!
    func moveTextField(_ textField: UITextField, moveDistance: Int, up: Bool) {
        let moveDuration = 0.3
        let movement: CGFloat = CGFloat(up ? moveDistance : -moveDistance)
        
        UIView.beginAnimations("animateTextField", context: nil)
        UIView.setAnimationBeginsFromCurrentState(true)
        UIView.setAnimationDuration(moveDuration)
        self.view.frame = self.view.frame.offsetBy(dx: 0, dy: movement)
        UIView.commitAnimations()
    }
    
    //.....
    
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    //override var prefersStatusBarHidden: Bool {
    //      return true
    // }


    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

  }



// im Summeneingabe nur zahlen und mit Komma getrennt und nur zwei Nochkommastellen
extension String {
    func isValidDouble(maxDecimalPlaces: Int) -> Bool {
        
        let formatter = NumberFormatter()
        formatter.allowsFloats = true
        let decimalSeparator = formatter.decimalSeparator ?? "."  
        
        
        if formatter.number(from: self) != nil {
            
            let split = self.components(separatedBy: decimalSeparator)
            
            let digits = split.count == 2 ? split.last ?? "" : ""
            
            
            return digits.count <= maxDecimalPlaces
        }
        
        return false
    }
}






