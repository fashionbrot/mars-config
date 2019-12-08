
var value, orig1, orig2, dv, panes = 2, highlight = true, connect = "align", collapse = false;
function initUI(id,value,orig2,mode) {
    if (value == null) return;
    var target = document.getElementById(id);
    target.innerHTML = "";
    dv = CodeMirror.MergeView(target, {
        value: value,
        origLeft: panes == 3 && !collapse && !connect ? orig1 : null,
        orig: orig2,
        theme:'abcdef',
        lineNumbers: true,
        mode: mode,
        styleActiveLine: true,
        matchBrackets: true,
        highlightDifferences: highlight,
        connect: null,
        collapseIdentical: collapse,
        revertButtons:false
    });
}

function toggleDifferences() {
    dv.setShowDifferences(highlight = !highlight);
}

window.onload = function() {
   /* $('#diffModal').modal('show');
    value = document.documentElement.innerHTML;
    orig1 = "<!doctype html>\n\n" + value.replace(/\.\.\//g, "codemirror/").replace("yellow", "orange");
    orig2 = value.replace(/\u003cscript/g, "\u003cscript type=text/javascript ")
        .replace("white", "purple;\n      font: comic sans;\n      text-decoration: underline;\n      height: 15em");
    initUI("editDiff",value,"3w53wrwerwe",'textile');
    var d = document.createElement("div");
    d.style.cssText = "width: 50px; margin: 7px; height: 14px";
    dv.editor().addLineWidget(57, d)*/
};

function mergeViewHeight(mergeView) {
    function editorHeight(editor) {
        if (!editor) return 0;
        return editor.getScrollInfo().height;
    }
    return Math.max(editorHeight(mergeView.leftOriginal()),
        editorHeight(mergeView.editor()),
        editorHeight(mergeView.rightOriginal()));
}

function resize(mergeView) {
    var height = mergeViewHeight(mergeView);
    for(;;) {
        if (mergeView.leftOriginal())
            mergeView.leftOriginal().setSize(null, height);
        mergeView.editor().setSize(null, height);
        if (mergeView.rightOriginal())
            mergeView.rightOriginal().setSize(null, height);

        var newHeight = mergeViewHeight(mergeView);
        if (newHeight >= height) break;
        else height = newHeight;
    }
    mergeView.wrap.style.height = height + "px";
}