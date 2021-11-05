//
//  AllBillsViewController.swift
//  Billtracker
//
//  Created by Valdet Dodaj on 29.01.18.
//  Copyright © 2018 Valdet Dodaj. All rights reserved.
//

import UIKit

class AllBillsViewController: UIPageViewController {
    @IBOutlet weak var pageControl: UIPageControl!
    
    var contentViewControllers = [UIViewController]()
    var titles = ["Categories","All Bills"]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        dataSource = self
        delegate = self
        
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
        let latestViewController = storyboard.instantiateViewController(withIdentifier: "ContentViewController") as! ContentViewController
        let allViewController = storyboard.instantiateViewController(withIdentifier: "ContentViewController") as! ContentViewController
        
        contentViewControllers = [latestViewController,allViewController]
        setViewControllers([contentViewControllers[0]], direction: .forward, animated: false, completion: nil)
        title = titles[0]
    }
    
}

extension AllBillsViewController: UIPageViewControllerDataSource, UIPageViewControllerDelegate {
    
    func pageViewController(_ pageViewController: UIPageViewController, viewControllerBefore viewController: UIViewController) -> UIViewController? {
        if let index = contentViewControllers.index(of: viewController) {
            let newIndex = index - 1
            if newIndex < 0 {
                return nil
            } else {
                return contentViewControllers[newIndex]
            }
        }
        return nil
    }
    
    func pageViewController(_ pageViewController: UIPageViewController, viewControllerAfter viewController: UIViewController) -> UIViewController? {
        if let index = contentViewControllers.index(of: viewController) {
            let newIndex = index + 1
            if newIndex >= contentViewControllers.count {
                return nil
            } else {
                return contentViewControllers[newIndex]
            }
        }
        return nil
    }
    
    func pageViewController(_ pageViewController: UIPageViewController, didFinishAnimating finished: Bool, previousViewControllers: [UIViewController], transitionCompleted completed: Bool) {
        if completed {
            if let currentViewController = pageViewController.viewControllers?.first {
                if let index = contentViewControllers.index(of: currentViewController) {
                    title = titles[index]
                    pageControl.currentPage = index
                }
            }
        }
    }
    

    
    


}
