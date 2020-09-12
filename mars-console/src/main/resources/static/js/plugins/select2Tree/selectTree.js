
/**
 * 基于select2和ztree的树形下拉框
 *
 * @param options
 *            object类型的select2配置参数{...,ztree:{setting:{},zNodes:[]},valueField:值属性名称,textField:文本属性名称}
 * @version: v1.0.0
 */
!function($) {
    'use strict';
    function print(obj) {
        console.dir('------------');
        console.dir(obj);
    }
    // 定义数据适配器
    $.fn.select2.amd.define('select2/data/ztree', [ './base', '../utils', 'jquery' ], function(BaseAdapter, Utils, $) {
        function ZtreeSelectAdapter($element, options) {
            this.$element = $element;
            this.options = options;
            ZtreeSelectAdapter.__super__.constructor.call(this);
        }
        Utils.Extend(ZtreeSelectAdapter, BaseAdapter);
        //当前选中的选项：通过选中的option找到对应的ztreenode，并将znode设置为选中状态
        ZtreeSelectAdapter.prototype.current = function(callback) {
            var data = [];
            var self = this;
            this.$element.find(':selected').each(function() {
                var $option = $(this);
                var option = self.item($option);
                option.tId = $option.data('tId');
                data.push(option);
            });

            // 从ztree获取当前选中的数据
            var $ztree = this.container.results.$ztree;
            for (var i = 0; i < data.length; i++) {
                var node = $ztree.getNodeByTId(data[i].tId);
                if (!node) {
                    continue;
                }
                data[i] = node;
                $ztree.selectNode(node, false, false);
            }
            callback(data);
        };

        ZtreeSelectAdapter.prototype.select = function(data) {
            var self = this;
            data.selected = true;
            var val = data.id;
            this.$element.val(val);
            this.$element.trigger('change');
        };

        ZtreeSelectAdapter.prototype.unselect = function(data) {};

        ZtreeSelectAdapter.prototype.bind = function(container, $container) {
            var self = this;
            this.container = container;
            container.on('select', function(params) {
                self.select(params.data);
            });
        };

        ZtreeSelectAdapter.prototype.destroy = function() {
            this.$element.find('*').each(function() {
                Utils.RemoveData(this);
            });
        };

        //搜索
        ZtreeSelectAdapter.prototype.query = function(params, callback) {
            var preText = this.$element.data('ztree-search');
            var text = $.trim(params.term);
            this.$element.data('ztree-search', text);
            //首次搜索，并且本次搜索的内容是空字符，不作处理
            if (!preText && !text) {
                callback({
                    results : []
                });
                return;
            }

            var data = [];
            var $ztree = this.container.results.$ztree;
            //无ztree对象，不作处理
            if (!$ztree) {
                callback({
                    results : []
                });
                return;
            }

            //ztree模糊搜索
            data = $ztree.fuzzySearch(text);

            callback({
                results : data
            });
        };
        ZtreeSelectAdapter.prototype.addOptions = function($options) {
            Utils.appendMany(this.$element, $options);
        };
        ZtreeSelectAdapter.prototype.option = function(data) {};

        ZtreeSelectAdapter.prototype.item = function($option) {
            var data = {};
            data = Utils.GetData($option[0], 'data');
            if (data != null) {
                return data;
            }
            if ($option.is('option')) {
                data = {
                    id : $option.val(),
                    text : $option.text(),
                    disabled : $option.prop('disabled'),
                    selected : $option.prop('selected'),
                    title : $option.prop('title')
                };
            } else if ($option.is('optgroup')) {
                data = {
                    text : $option.prop('label'),
                    children : [],
                    title : $option.prop('title')
                };
                var $children = $option.children('option');
                var children = [];
                for (var c = 0; c < $children.length; c++) {
                    var $child = $($children[c]);
                    var child = this.item($child);
                    children.push(child);
                }
                data.children = children;
            }
            data = this._normalizeItem(data);
            data.element = $option[0];
            Utils.StoreData($option[0], 'data', data);
            return data;
        };

        ZtreeSelectAdapter.prototype._normalizeItem = function(item) {
            if (item !== Object(item)) {
                item = {
                    id : item,
                    text : item
                };
            }
            item = $.extend({}, {
                text : ''
            }, item);

            var defaults = {
                selected : false,
                disabled : false
            };

            if (item.id != null) {
                item.id = item.id.toString();
            }

            if (item.text != null) {
                item.text = item.text.toString();
            }

            if (item._resultId == null && item.id && this.container != null) {
                item._resultId = this.generateResultId(this.container, item);
            }

            return $.extend({}, defaults, item);
        };

        ZtreeSelectAdapter.prototype.matches = function(params, data) {
            var matcher = this.options.get('matcher');

            return matcher(params, data);
        };

        return ZtreeSelectAdapter;
    });
    // 单选适配器
    $.fn.select2.amd.define('select2/selection/ztreeSingle', [ 'jquery', './base', '../utils' ], function($, BaseSelection, Utils) {
        function ZtreeSingleSelection() {
            ZtreeSingleSelection.__super__.constructor.apply(this, arguments);
        }
        Utils.Extend(ZtreeSingleSelection, BaseSelection);
        ZtreeSingleSelection.prototype.render = function() {
            var $selection = ZtreeSingleSelection.__super__.render.call(this);
            $selection.addClass('select2-selection--single');
            $selection.html('<span class="select2-selection__rendered"></span>' + '<span class="select2-selection__arrow" role="presentation">' + '<b role="presentation"></b>'
                + '</span>');
            return $selection;
        };
        ZtreeSingleSelection.prototype.bind = function(container, $container) {
            var self = this;
            ZtreeSingleSelection.__super__.bind.apply(this, arguments);
            var id = container.id + '-container';
            this.$selection.find('.select2-selection__rendered').attr('id', id).attr('role', 'textbox').attr('aria-readonly', 'true');
            this.$selection.attr('aria-labelledby', id);
            this.$selection.on('mousedown', function(evt) {
                // 只响应鼠标左键事件
                if (evt.which !== 1) {
                    return;
                }
                self.trigger('toggle', {
                    originalEvent : evt
                });
            });
            this.$selection.on('focus', function(evt) {

            });
            this.$selection.on('blur', function(evt) {
            });
            container.on('focus', function(evt) {
                if (!container.isOpen()) {
                    self.$selection.trigger('focus');
                }
            });
        };
        ZtreeSingleSelection.prototype.clear = function() {
            var $rendered = this.$selection.find('.select2-selection__rendered');
            $rendered.empty();
            $rendered.removeAttr('title'); // clear tooltip on
            // empty
        };
        ZtreeSingleSelection.prototype.display = function(data, container) {
            var template = this.options.get('templateSelection');
            var escapeMarkup = this.options.get('escapeMarkup');
            var str = template(data, container);
            return escapeMarkup(str);
        };

        ZtreeSingleSelection.prototype.selectionContainer = function() {
            return $('<span></span>');
        };

        ZtreeSingleSelection.prototype.update = function(data) {
            if (data.length === 0) {
                this.clear();
                return;
            }
            var selection = data[0];
            var $rendered = this.$selection.find('.select2-selection__rendered');
            var formatted = this.display(selection, $rendered);
            $rendered.empty().append(formatted);
            $rendered.attr('title', selection[this.options.get('textField')]);
        };

        return ZtreeSingleSelection;
    });
    // 定义结果展示适配器
    $.fn.select2.amd.define('select2/ztreeresults', [ 'jquery', './utils' ], function($, Utils) {
        function ZtreeResults($element, options, dataAdapter) {
            this.$element = $element;
            this.data = dataAdapter;
            this.options = options;
            ZtreeResults.__super__.constructor.call(this);
        }
        Utils.Extend(ZtreeResults, Utils.Observable);
        //初始化ztree
        ZtreeResults.prototype.render = function() {
            var $results = $('<ul class="select2-results__options ztree" role="tree"></ul>');
            if (this.options.get('multiple')) {
                $results.attr('aria-multiselectable', 'true');
            }
            this.$results = $results;
            // 初始化ztree和下拉选项，以便通过select元素的option找到对应的znode
            if (!this.$ztree) {
                var config = this.options.get('ztree');
                this.$ztree = $.fn.zTree.init(this.$results, config.setting, config.zNodes);
                var data = this.$ztree.transformToArray(this.$ztree.getNodes());
                // 添加1个空元素
                var valueField = this.options.get('valueField');
                var textField = this.options.get('textField');
                // 利用节点数据生成select-->option
                for (var i = 0; i < data.length; i++) {
                    var id = data[i][valueField];
                    var $option = $('<option value="' + id + '" data-select2-id="' + id + '">' + data[i][textField] + '</option>');
                    $option.data('tId', data[i].tId);
                    this.$element.append($option);
                }
            }

            return $results;
        };

        ZtreeResults.prototype.clear = function() {};

        ZtreeResults.prototype.displayMessage = function(params) {
            var escapeMarkup = this.options.get('escapeMarkup');
            this.clear();
            this.hideLoading();
            var $message = $('<li role="treeitem" aria-live="assertive"' + ' class="select2-results__option"></li>');
            var message = this.options.get('translations').get(params.message);
            $message.append(escapeMarkup(message(params.args)));
            $message[0].className += ' select2-results__message';
            this.$results.append($message);
        };
        ZtreeResults.prototype.hideMessages = function() {
            this.$results.find('.select2-results__message').remove();
        };
        // 展示下拉选项
        ZtreeResults.prototype.append = function(data) {
            this.hideLoading();
        };

        ZtreeResults.prototype.position = function($results, $dropdown) {
            var $resultsContainer = $dropdown.find('.select2-results');
            $resultsContainer.append($results);
        };

        ZtreeResults.prototype.sort = function(data) {
            return data;
        };
        //不作处理
        ZtreeResults.prototype.highlightFirstItem = function(){};
        //不作处理
        ZtreeResults.prototype.setClasses = function() {};

        ZtreeResults.prototype.showLoading = function(params) {
            this.hideLoading();
            var loadingMore = this.options.get('translations').get('searching');
            var loading = {
                disabled : true,
                loading : true,
                text : loadingMore(params)
            };
            var $loading = this.option(loading);
            $loading.className += ' loading-results';
            this.$results.prepend($loading);
        };

        ZtreeResults.prototype.hideLoading = function() {
            this.$results.find('.loading-results').remove();
        };

        ZtreeResults.prototype.option = function(data) {
            var option = document.createElement('li');
            option.className = 'select2-results__option';
            var attrs = {
                'role' : 'treeitem',
                'aria-selected' : 'false'
            };
            var matches = window.Element.prototype.matches || window.Element.prototype.msMatchesSelector || window.Element.prototype.webkitMatchesSelector;
            if (data.title) {
                option.title = data.title;
            }
            this.template(data, option);
            Utils.StoreData(option, 'data', data);

            return option;
        };

        ZtreeResults.prototype.bind = function(container, $container) {
            var self = this;
            var id = container.id + '-results';
            this.$results.attr('id', id);
            container.on('results:all', function(params) {
                self.clear();
                self.append(params.data);
            });
            container.on('results:append', function(params) {
                self.append(params.data);
                if (container.isOpen()) {
                    self.setClasses();
                }
            });
            container.on('query', function(params) {
                self.hideMessages();
                self.showLoading(params);
            });
            container.on('select', function() {
                if (!container.isOpen()) {
                    return;
                }
                self.setClasses();
            });
            container.on('open', function() {
                self.$results.attr('aria-expanded', 'true');
                self.$results.attr('aria-hidden', 'false');
                self.setClasses();
                self.ensureHighlightVisible();
            });
            container.on('close', function() {
                self.$results.attr('aria-expanded', 'false');
                self.$results.attr('aria-hidden', 'true');
                self.$results.removeAttr('aria-activedescendant');
            });
            container.on('results:toggle', function() {
                var $highlighted = self.getHighlightedResults();
                if ($highlighted.length === 0) {
                    return;
                }
                $highlighted.trigger('mouseup');
            });
            container.on('results:select', function() {
                var $highlighted = self.getHighlightedResults();
                if ($highlighted.length === 0) {
                    return;
                }
                var data = Utils.GetData($highlighted[0], 'data');
                if ($highlighted.attr('aria-selected') == 'true') {
                    self.trigger('close', {});
                } else {
                    self.trigger('select', {
                        data : data
                    });
                }
            });
            if ($.fn.mousewheel) {
                this.$results.on('mousewheel', function(e) {
                    var top = self.$results.scrollTop();
                    var bottom = self.$results.get(0).scrollHeight - top + e.deltaY;
                    var isAtTop = e.deltaY > 0 && top - e.deltaY <= 0;
                    var isAtBottom = e.deltaY < 0 && bottom <= self.$results.height();
                    if (isAtTop) {
                        self.$results.scrollTop(0);
                        e.preventDefault();
                        e.stopPropagation();
                    } else if (isAtBottom) {
                        self.$results.scrollTop(self.$results.get(0).scrollHeight - self.$results.height());
                        e.preventDefault();
                        e.stopPropagation();
                    }
                });
            }

            this.$results.on('mouseup', '.select2-results__option[aria-selected]', function(evt) {
                var $this = $(this);
                var data = Utils.GetData(this, 'data');
                if ($this.attr('aria-selected') === 'true') {
                    if (self.options.get('multiple')) {
                        self.trigger('unselect', {
                            originalEvent : evt,
                            data : data
                        });
                    } else {
                        self.trigger('close', {});
                    }
                    return;
                }
                self.trigger('select', {
                    originalEvent : evt,
                    data : data
                });
            });
        };

        ZtreeResults.prototype.getHighlightedResults = function() {
            return [];
        };

        ZtreeResults.prototype.destroy = function() {
            this.$results.remove();
        };
        ZtreeResults.prototype.ensureHighlightVisible = function() {};

        ZtreeResults.prototype.template = function(result, container) {
            var template = this.options.get('templateResult');
            var escapeMarkup = this.options.get('escapeMarkup');
            var content = template(result, container);
            if (content == null) {
                container.style.display = 'none';
            } else if (typeof content === 'string') {
                container.innerHTML = escapeMarkup(content);
            } else {
                $(container).append(content);
            }
        };

        return ZtreeResults;
    });

    // 定义组件
    $.fn.select2.amd.define("jquery.select2ztree", [ 'jquery', 'jquery-mousewheel', './select2/core', './select2/defaults', './select2/utils', "./select2/ztreeresults",
        "select2/selection/ztreeSingle", "select2/data/ztree" ], function($, _, Select2, Defaults, Utils, ZtreeResults,
                                                                          ZtreeSingleSelection, ZtreeSelectAdapter) {
        if ($.fn.select2ztree) {
            return Select2;
        }
        var thisMethods = [ 'open', 'close', 'destroy' ];
        // 定义jquery对象名称
        $.fn.select2ztree = function(options) {
            options = options || {};
            if (typeof options === 'object') {
                // 初始化属性
                // 值属性名称
                options.valueField = options.valueField || 'id';
                // 文本属性名称
                options.textField = options.textField || 'text';
                options.resultsAdapter = ZtreeResults;
                if ($(this).attr('multiple')) {
                    options.selectionAdapter = ZtreeMultiSelection;
                } else {
                    options.selectionAdapter = ZtreeSingleSelection;
                }
                options.dataAdapter = ZtreeSelectAdapter;
                options.templateSelection = function(selection) {
                    // 模糊搜索控件会将节点名称的实际值保存在oldname属性中
                    if (selection.oldname) {
                        return selection.oldname;
                    }
                    return selection[options.textField];
                };
                if (!options.ztree || !options.ztree.setting) {
                    throw new Error('缺少ztree配置: {ztree:{setting:{},zNodes:[]}}');
                }
                if(options.ztree.zNodes){
                    //禁止节点进行url跳转
                    $.each(options.ztree.zNodes,function(idx,ele){
                        ele.url = '';
                    });
                }
                //覆盖视图配置
                options.ztree.setting.view={
                    selectedMulti : false,
                    dblClickExpand : false
                }
                var self = this;
                // 重写ztree点击事件：点击之后用于触发select2相关操作
                options.ztree.setting.callback = {
                    onClick : function(event, treeId, treeNode, clickFlag) {
                        // 触发select2的选择操作
                        var data = [];
                        data.push(treeNode[options.valueField]);
                        self.select2ztree('trigger', 'select', {
                            data : treeNode,
                            originalEvent : event
                        });
                    }
                };
                this.each(function() {
                    var instanceOptions = $.extend(true, {}, options);
                    new Select2($(this), instanceOptions);
                });
                return this;
            } else if (typeof options === 'string') {
                var ret;
                var args = Array.prototype.slice.call(arguments, 1);
                this.each(function() {
                    var instance = Utils.GetData(this, 'select2');
                    if (instance == null && window.console && console.error) {
                        console.error('select2ztree(\'' + options + '\') method was called on an ' + 'element that is not using Select2.');
                    }
                    ret = instance[options].apply(instance, args);
                });
                if ($.inArray(options, thisMethods) > -1) {
                    return this;
                }
                return ret;
            } else {
                throw new Error('错误的配置参数：' + options);
            }
        };

        $.fn.select2ztree.defaults = Defaults;

        return Select2;
    });
    jQuery.fn.select2.amd.require('jquery.select2ztree');
}(jQuery);
