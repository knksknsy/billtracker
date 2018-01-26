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
    
     // verbindet alle weiteren Objekte, die zur Bildaufnahme,- anzeige und -speicherung erforderlich sind
    let captureSession = AVCaptureSession()
    var photoOut = AVCapturePhotoOutput()
    //var photoOut = AVCapturePhotoOutput()
    
    var videoInput: AVCaptureDeviceInput!
    var previewLayer: AVCaptureVideoPreviewLayer!
    //var useRearCamera = true
    
    //var videoPreviewLayer: AVCaptureVideoPreviewLayer?
    //.....
    //var capturePhotoOutput: AVCapturePhotoOutput?
    //...
    
    
    
    // hier werden Fotos gespeichert
    //var photos = [UIImage]()
    
    

    override func viewDidLoad() {
        super.viewDidLoad()

        
        // Set the output on the capture session
        ///captureSession.addOutput (photoOut)

        
        sumInput.delegate = self
        
        
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
        
        
        let photoSettings : AVCapturePhotoSettings!
        photoSettings = AVCapturePhotoSettings.init(format: [AVVideoCodecKey: AVVideoCodecType.jpeg])
        photoSettings.isAutoStillImageStabilizationEnabled = true
        photoSettings.flashMode = .auto
        photoSettings.isHighResolutionPhotoEnabled = false
        
        self.photoOut.capturePhoto(with: photoSettings, delegate: self)
        //captureSession.sessionPreset = AVCaptureSession.Preset.photo
        captureSession.stopRunning()


   }
    
    @IBAction func savePhoto(_ sender: Any) {
        
        
    }
    
    
    
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
    
    
    
    // für iOS 10
    
//    func photoOutput(_ captureOutput: AVCapturePhotoOutput,
//                     didFinishProcessingPhoto photoSampleBuffer: CMSampleBuffer?,
//                     previewPhoto previewPhotoSampleBuffer: CMSampleBuffer?,
//                 resolvedSettings: AVCaptureResolvedPhotoSettings,
//                 bracketSettings: AVCaptureBracketedStillImageSettings?,
//                 error: Error?) {
//        // get captured image
//
//        guard error == nil,
//            let photoSampleBuffer = photoSampleBuffer else {
//                print("Error capturing photo: \(String(describing: error))")
//                return
//        }
//
//        // Convert photo same buffer to a jpeg image data by using // AVCapturePhotoOutput
//        guard let imageData =
//            AVCapturePhotoOutput.jpegPhotoDataRepresentation(forJPEGSampleBuffer: photoSampleBuffer, previewPhotoSampleBuffer: previewPhotoSampleBuffer) else {
//                return
//        }
//
//
//        let capturedImage = UIImage.init(data: imageData , scale: 1.0)
//        if let image = capturedImage {
//            // Save our captured image to photos album
//            UIImageWriteToSavedPhotosAlbum(image, nil, nil, nil)
//        }
//
//    }
    
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // CaptureSession pausieren
        //captureSession.stopRunning()
    }
    

    
    
//    func setupCaptureSession() {
//        captureSession.sessionPreset = AVCaptureSession.Preset.photo
//    }

    
//    func setupDevice() {
//        let deviceDicoverySession = AVCaptureDevice.DiscoverySession(deviceTypes: [AVCaptureDevice.DeviceType.builtInWideAngleCamera], mediaType: AVMediaType.video, position: AVCaptureDevice.Position.unspecified)
//        // AVCaptureDevice.Position.back
//        let devices = deviceDicoverySession.devices
//        for device in devices {
//            if device.position == AVCaptureDevice.Position.back{
//                backCamera = device
//
//            } else if device.position == AVCaptureDevice.Position.front {
//                frontCamera = device
//
//            }
//        }
//        currentCamera = backCamera
//    }


//    func setupInputOutput() {
//        do {
//            let captureDeviceInput = try AVCaptureDeviceInput(device: currentCamera!)
//            captureSession.addInput(captureDeviceInput)
//            photoOutput = AVCapturePhotoOutput()
//            photoOutput?.setPreparedPhotoSettingsArray([AVCapturePhotoSettings(format: [AVVideoCodecKey: AVVideoCodecType.jpeg])], completionHandler: nil)
//            captureSession.addOutput(photoOutput!)
//        } catch  {
//            print(error)
//        }
//
//    }

//    func setuptPreviewLayer() {
//        cameraPreviewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
//        cameraPreviewLayer?.videoGravity = AVLayerVideoGravity.resizeAspectFill
//        cameraPreviewLayer?.connection?.videoOrientation = AVCaptureVideoOrientation.portrait
//        cameraPreviewLayer?.frame = self.view.frame
//        self.view.layer.insertSublayer(cameraPreviewLayer!, at: 0)
//    }


//    func startRunningCaptureSession() {
//        captureSession.startRunning()
//
//    }

    
    // Textfeld: Beim Drücken von Enter soll die Tastatur verschwinden
    
   

    
   //****** ab hier Textfeld
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        // User pressed the delete-key to remove a character, this is always valid, return true to allow change
        if string.isEmpty { return true }
        
        
        let currentText = sumInput.text ?? ""
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



// im Summeneingabe nur zahlen und mit komma getrennt und nur zwei Nochkommastellen
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
        
        return false // couldn't turn string into a valid number
    }
}






