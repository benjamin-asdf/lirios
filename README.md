# lirios

Visit:

https://benjamin-asdf.github.io/lirios/

# Dev server

```sh
bb dev
```

Visit http://localhost:1337

Then `cider-connect-clj` localhost 1339.

```elisp
(defun mm/connect-clj-scittle ()
  (interactive)
  (cider-connect-clj '(:host "localhost" :port 1339)))
```


# favicon.ico

Generated with imagemagick

```sh
convert circle.svg -resize x16 -gravity center -crop 16x16+0+0 +repage favicon.ico
```

