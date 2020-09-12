/**
 * ztree模糊搜索
 *
 * @version: v1.0.0
 */
!function($) {
    /**
     * @param searchText
     *            模糊搜索文本内容
     * @param isHighLight
     *            是否高亮,默认高亮,传入false禁用
     * @param isExpand
     *            是否展开,默认合拢,传入true展开
     *
     * @returns
     */
    var _init = $.fn.zTree.init;
    $.fn.zTree.init = function(obj, zSetting, zNodes) {
        var zTreeTools = _init(obj, zSetting, zNodes);
        // 为ztree对象添加模糊搜索方法
        zTreeTools.fuzzySearch = function(searchText, isHighLight, isExpand) {
            var zTreeObj = this;
            var nameKey = zTreeObj.setting.data.key.name;
            isHighLight = isHighLight === false ? false : true;
            isExpand = isExpand ? true : false;
            zTreeObj.setting.view.nameIsHTML = isHighLight;
            var metaChar = '[\\[\\]\\\\\^\\$\\.\\|\\?\\*\\+\\(\\)]';
            var rexMeta = new RegExp(metaChar, 'gi');
            function ztreeFilter(zTreeObj, _keywords, callBackFunc) {
                if (!_keywords) {
                    _keywords = '';
                }
                function filterFunc(node) {
                    if (node && node.oldname && node.oldname.length > 0) {
                        node[nameKey] = node.oldname;
                    }
                    zTreeObj.updateNode(node);
                    if (_keywords.length == 0) {
                        zTreeObj.showNode(node);
                        zTreeObj.expandNode(node, isExpand);
                        return true;
                    }
                    if (node[nameKey] && node[nameKey].toLowerCase().indexOf(_keywords.toLowerCase()) != -1) {
                        if (isHighLight) {
                            var newKeywords = _keywords.replace(rexMeta, function(matchStr) {
                                return '\\' + matchStr;
                            });
                            node.oldname = node[nameKey];
                            var rexGlobal = new RegExp(newKeywords, 'gi');
                            node[nameKey] = node.oldname.replace(rexGlobal, function(originalText) {
                                var highLightText = '<span style="color: whitesmoke;background-color: darkred;">' + originalText + '</span>';
                                return highLightText;
                            });
                            zTreeObj.updateNode(node);
                        }
                        zTreeObj.showNode(node);
                        return true;
                    }

                    zTreeObj.hideNode(node);
                    return false;
                }

                var nodesShow = zTreeObj.getNodesByFilter(filterFunc);
                processShowNodes(nodesShow, _keywords);
                return nodesShow;
            }

            /**
             * 展示搜索结果前处理节点
             */
            function processShowNodes(nodesShow, _keywords) {
                if (nodesShow && nodesShow.length > 0) {
                    if (_keywords.length > 0) {
                        $.each(nodesShow, function(n, obj) {
                            var pathOfOne = obj.getPath();
                            if (pathOfOne && pathOfOne.length > 0) {
                                for (var i = 0; i < pathOfOne.length - 1; i++) {
                                    zTreeObj.showNode(pathOfOne[i]);
                                    zTreeObj.expandNode(pathOfOne[i], true);
                                }
                            }
                        });
                    } else {
                        var rootNodes = zTreeObj.getNodesByParam('level', '0');
                        $.each(rootNodes, function(n, obj) {
                            zTreeObj.expandNode(obj, true);
                        });
                    }
                }
            }
            return ztreeFilter(zTreeObj, searchText);
        }
        return zTreeTools;
    };
}(jQuery);