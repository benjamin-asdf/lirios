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

(defn gen-art [state]
  (let [new-art (repeatedly 10 random-shape)
        new-history
        (conj (-> state :art :history) new-art)
        new-cursor (dec (count new-history))]
    (assoc
     state
     :art
     {:cursor new-cursor
      :history new-history})))

(def state
  (r/atom
   (gen-art {:art {:history[]}})))

(defn current-art [state]
  (let [where (-> state :art :cursor)]
    (-> state :art :history (nth where))))

(defn where-ui [state]
  [:span
   {:style {:margin "5px"}}
   [:span (inc (-> state :art :cursor))]
   [:span " / "]
   [:span (-> state :art :history count)]])

(defn view []
  (fn []
    [:div
     [click-counter]
     [:div
      [:svg {:width "100%" :height "400px"}
       (current-art @state)]
      [:button {:on-click (fn [] (swap! state gen-art))} "Generate Art!"]
      [:div
       [:button {:on-click (fn [] (swap! state update-in [:art :cursor] (fn [n] (max 0 (dec n)))))} "<"]
       [where-ui @state]
       [:button {:on-click (fn []
                             (swap!
                              state
                              (fn [state]
                                (let [curr-n (-> state :art :cursor)
                                      max-n (dec (-> state :art :history count))
                                      next-n (min max-n (inc curr-n))]
                                  (assoc-in state [:art :cursor] next-n)))))}
        ">"]]]
     [:div]]))

(rdom/render [view] (.getElementById js/document "app"))
