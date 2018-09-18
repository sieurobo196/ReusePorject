Array.prototype.remove = function(item) {
	var j = 0;
	while (j < this.length) {
		// alert(originalArray[j]);
		if (this[j] == item) {
		this.splice(j, 1);
		} else { j++; }
	}
};


