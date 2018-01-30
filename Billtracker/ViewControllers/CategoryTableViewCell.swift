//
//  CategoryTableViewCell.swift
//  Billtracker
//
//  Created by Kaan K. on 30.01.18.
//  Copyright © 2018 Valdet D.. All rights reserved.
//

import UIKit

class CategoryTableViewCell: UITableViewCell {
    
    var firebaseHelper = FirebaseHelper()
    
    @IBOutlet weak var categoryLabel: UILabel!
    
    @IBAction func deleteCategory(_ sender: Any) {
        firebaseHelper.deleteCategory(category: categoryLabel.text!)
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }


    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    

}
