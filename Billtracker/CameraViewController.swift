//
//  CameraViewController.swift
//  Billtracker
//
//  Created by Valdet D. on 10.12.17.
//  Copyright © 2017 Valdet D.. All rights reserved.
//

import UIKit
import AVFoundation
class CameraViewController: UIViewController {

    
    //var captureSession: AVCaptureSession?
    var captureSession = AVCaptureSession()
    var backCamera: AVCaptureDevice?
    var frontCamera: AVCaptureDevice?
    var currentCamera: AVCaptureDevice?
    var photoOutput: AVCapturePhotoOutput?

    var cameraPreviewLayer: AVCaptureVideoPreviewLayer?
    var image: UIImage?
    //var videoPreviewLayer: AVCaptureVideoPreviewLayer?
    //captureSession = AVCaptureSession()

    override func viewDidLoad() {
        super.viewDidLoad()

        setupCaptureSession()
        setupDevice()
        setupInputOutput()
        setuptPreviewLayer()
        startRunningCaptureSession()

        // Do any additional setup after loading the view, typically from a nib.


    }
    
    
    @IBAction func takePhoto(_ sender: Any) {
        //let settings = AVCapturePhotoSettings()
        //photoOutput?.capturePhoto(with: settings, delegate: self)
    }
    
//    
//    @IBAction func savePhoto(_ sender: Any) {
//        UISaveVideoAtPathToSavedPhotosAlbum(<#T##videoPath: String##String#>, <#T##completionTarget: Any?##Any?#>, <#T##completionSelector: Selector?##Selector?#>, <#T##contextInfo: UnsafeMutableRawPointer?##UnsafeMutableRawPointer?#>)
//    }
    
    
    func setupCaptureSession() {
        captureSession.sessionPreset = AVCaptureSession.Preset.photo
    }

    
    func setupDevice() {
        let deviceDicoverySession = AVCaptureDevice.DiscoverySession(deviceTypes: [AVCaptureDevice.DeviceType.builtInWideAngleCamera], mediaType: AVMediaType.video, position: AVCaptureDevice.Position.unspecified)
        // AVCaptureDevice.Position.back
        let devices = deviceDicoverySession.devices
        for device in devices {
            if device.position == AVCaptureDevice.Position.back{
                backCamera = device

            } else if device.position == AVCaptureDevice.Position.front {
                frontCamera = device

            }
        }
        currentCamera = backCamera
    }


    func setupInputOutput() {
        do {
            let captureDeviceInput = try AVCaptureDeviceInput(device: currentCamera!)
            captureSession.addInput(captureDeviceInput)
            photoOutput = AVCapturePhotoOutput()
            photoOutput?.setPreparedPhotoSettingsArray([AVCapturePhotoSettings(format: [AVVideoCodecKey: AVVideoCodecType.jpeg])], completionHandler: nil)
            captureSession.addOutput(photoOutput!)
        } catch  {
            print(error)
        }

    }

    func setuptPreviewLayer() {
        cameraPreviewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        cameraPreviewLayer?.videoGravity = AVLayerVideoGravity.resizeAspectFill
        cameraPreviewLayer?.connection?.videoOrientation = AVCaptureVideoOrientation.portrait
        cameraPreviewLayer?.frame = self.view.frame
        self.view.layer.insertSublayer(cameraPreviewLayer!, at: 0)
    }


    func startRunningCaptureSession() {
        captureSession.startRunning()

    }


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

extension ViewController: AVCapturePhotoCaptureDelegate {
    func photoOutput(_ output: AVCapturePhotoOutput, didFinishProcessingPhoto photo: AVCapturePhoto, error: Error?) {
        if let imageData = photo.fileDataRepresentation() {
            print(imageData)
        }
    }
}

