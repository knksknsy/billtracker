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


class CameraViewController: UIViewController, UITextFieldDelegate, UINavigationControllerDelegate, AVCapturePhotoCaptureDelegate {
    
    @IBOutlet weak var previewView: UIView!
    @IBOutlet weak var sumInput: UITextField!
    @IBOutlet weak var categoryInput: UITextField!
    @IBOutlet weak var captureButton: UIBarButtonItem!
    @IBOutlet weak var savePhoto: UIBarButtonItem!
    //@IBOutlet weak var capturedPhoto: UIImageView!
    
    
    // verbindet alle weiteren Objekte, die zur Bildaufnahme,- anzeige und -speicherung erforderlich sind
    let captureSession = AVCaptureSession()
    var capturedImage: Data!
    var photoOut = AVCapturePhotoOutput()
    
    
    var videoInput: AVCaptureDeviceInput!
    var previewLayer: AVCaptureVideoPreviewLayer!
    //var useRearCamera = true
    
    var firebaseHelper: FirebaseHelper!
    
    
    // hier werden Fotos gespeichert
    //var photos = [UIImage]()
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        firebaseHelper = FirebaseHelper()
        
        
        // Set the output on the capture session
        //captureSession.addOutput (photoOut)
        
        
        sumInput.delegate = self
        //        savePhoto.isEnabled = false
        
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
    
    @IBAction func savePhoto(_ sender: Any) {
        captureSession.startRunning()
        let alert = UIAlertController(title: "Information", message: "Bild wurde erfolgreich gespeichert", preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .`default`, handler: { _ in
            NSLog("The \"OK\" alert occured.")
        }))
        let bill: Bill = Bill(category: categoryInput.text!, date: UInt(Date().timeIntervalSinceReferenceDate), sum: Double(sumInput.text!)!)
        firebaseHelper.createBill(bill, data: capturedImage)
        
    }
    
    //
    
    func photoOutput(_ output: AVCapturePhotoOutput,
                     didFinishProcessingPhoto photo: AVCapturePhoto,
                     error: Error?) {
        PHPhotoLibrary.shared().performChanges( {
            let creationRequest: PHAssetCreationRequest? = PHAssetCreationRequest.forAsset()
            creationRequest?.addResource(with: PHAssetResourceType.photo, data: photo.fileDataRepresentation()!, options: nil)
        }, completionHandler: nil)
        guard error == nil else {
            print("Error capturing photo: \(String(describing: error))")
            return
        }
        capturedImage = photo.fileDataRepresentation()!
    }
    
    //Textfield
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let currentText = sumInput.text ?? ""
        
        //        if (sumInput.hasText && categoryInput.hasText) {
        //            savePhoto.isEnabled = true
        //        } else if (!sumInput.hasText || !categoryInput.hasText){
        //            savePhoto.isEnabled = false
        //        }
        
        let replacementText = (currentText as NSString).replacingCharacters(in: range, with: string)
        
        return replacementText.isValidDouble(maxDecimalPlaces: 2)
    }
    
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        sumInput.resignFirstResponder()
        categoryInput.resignFirstResponder()
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
    
    
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
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






