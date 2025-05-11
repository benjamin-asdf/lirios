(ns
    main
    (:require
     [reagent.core :as r]
     [reagent.dom :as rdom]))

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
  (let [new-art (into [] (repeatedly 10 random-shape))
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
  (get-in state [:art :history (-> state :art :cursor)]))

(defn where-ui [state]
  [:span
   {:style {:margin "5px"}}
   [:span (inc (-> state :art :cursor))]
   [:span " / "]
   [:span (-> state :art :history count)]])

(defn file-input
  [{:keys [on-file-drop]}]
  [:input
   {:on-change (fn [e]
                 (when-let [file (-> e
                                     .-target
                                     .-files
                                     (aget 0))]
                   (let [reader (js/FileReader.)]
                     (.readAsText reader file)
                     (.addEventListener
                       reader
                       "load"
                       (fn [e]
                         (let [content (-> e
                                           .-target
                                           .-result)]
                           (on-file-drop content)))))))
    :type "file"}])

(defn view []
  (fn []
    [:div
     [file-input
      {:on-file-drop
       (fn [content]
         (def content content)
         (reset! state (read-string content)))}]
     ;; export state to file
     [:button
        {:on-click
         (fn []
           (let [content (pr-str @state)
                   blob (js/Blob. #js [content] #js {:type "text/plain"})
                   url (js/URL.createObjectURL blob)
                   a (.createElement js/document "a")]
             (set! (.-href a) url)
             (set! (.-download a) "state.edn")
             (.click a)))}
      "Export State"]
     [:div
      [:svg {:width "100%" :height "400px" :margin-top "5px"}
       (current-art @state)]
      [:button {:on-click (fn [] (swap! state gen-art))} "Generate Art!"]
      [:div
       [:button
        {:on-click
         (fn []
           (swap!
            state
            update-in
            [:art :cursor]
            (fn [n] (max 0 (dec n)))))} "<"]
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

;;
;; (js/setInterval
;;  (fn []
;;    (swap! state gen-art))
;;  500)

(comment
  (keys (deref state))
  (-> (deref state) :art :cursor)
  (deref state)

  state
  (def a (atom {}))
  (swap! a assoc :a 1)
  (swap! a update :a inc)

  ;; make a new art every 2 secocnds
  (current-art (deref state))
  (def state-t1 (deref state))
  (->  state-t1 :art :history count))
