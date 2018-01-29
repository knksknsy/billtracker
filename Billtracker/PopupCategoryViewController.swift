//
//  PopupCategoryViewController.swift
//  Billtracker
//
//  Created by Valdet D. on 28.01.18.
//  Copyright © 2018 Valdet D.. All rights reserved.
//

import UIKit

class PopupCategoryViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    
    @IBAction func btnCancel(_ sender: Any) {
        dismiss(animated: true) {
            
        }
    }
    
    @IBAction func btnSaveCategory(_ sender: Any) {
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
