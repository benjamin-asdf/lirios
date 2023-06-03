(ns
    main
    (:require
     [reagent.core :as r]
     [reagent.dom :as rdom]))

(defn click-counter []
  (let [counter (r/atom 0)]
    (fn []
      [:div
       (str "count: " @counter)
       [:div
        [:button
         {:on-click (fn [] (swap! counter inc))}
         "click!"]]])))

(defn random-shape []
  (let [shape (rand-nth ["circle" "rect"])
        x (rand-int 300)
        y (rand-int 300)
        size (rand-int 100)
        color (str "rgb(" (rand-int 256) ", " (rand-int 256) ", " (rand-int 256))]
    (cond
      (= shape "circle") ^{:key (str x y size color)} [:circle {:cx x :cy y :r (max 1 size) :fill color}]
      (= shape "rect") ^{:key (str x y size color)} [:rect {:x x :y y :width (max 1 size) :height (max 1 size) :fill color}])))

(defn view []
  (let [shapes (r/atom (repeatedly 10 random-shape))]
    (fn []
      [:div
       [click-counter]
       [:div
        [:svg {:width "100%" :height "400px"} @shapes]
        [:div
         [:button
          {:on-click
           (fn []
             (reset! shapes (repeatedly 10 random-shape)))}
          "Generate Art!"]]]
       [:div]])))

(rdom/render [view] (.getElementById js/document "app"))
