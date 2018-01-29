//
//  ViewController.swift
//  Billtracker
//
//  Created by Valdet D. on 22.11.17.
//  Copyright © 2017 Valdet D.. All rights reserved.
//

import UIKit

class ContentViewController: UIViewController {

    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
 
    }


}

extension ContentViewController: UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if let cell = tableView.dequeueReusableCell(withIdentifier: "Bllcell", for: indexPath) as? RecipeTableViewCell {
            return cell
        }
        return UITableViewCell()
}
