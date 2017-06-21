<!-- script src="//cdnjs.cloudflare.com/ajax/libs/d3/3.5.3/d3.min.js"></script -->
<script src="http://datamaps.github.io/scripts/topojson.js"></script>
<script src="http://datamaps.github.io/scripts/0.4.0/datamaps.all.js"></script>
<div id="worldMap" style="position: relative; width: 1200px; height: 1000px;"></div>
<script>
setTimeout(function(){
var map = new Datamap({
element: document.getElementById('worldMap'),
geographyConfig: {
popupOnHover: false,
highlightOnHover: false
},
fills: {
defaultFill: '#ABDDA4',
'0': '#393b79',
'1': '#5254a3',
'2': '#6b6ecf',
'3': '#9c9ede',
'4': '#637939',
'5': '#8ca252',
'6': '#b5cf6b',
'7': '#cedb9c',
'8': '#8c6d31',
'9': '#bd9e39',
'10': '#e7ba52',
'11': '#e7cb94',
'12': '#843c39',
'13': '#ad494a',
'14': '#d6616b',
'15': '#e7969c',
'16': '#7b4173',
'17': '#a55194',
'18': '#ce6dbd',
'19': '#de9ed6'
},
bubblesConfig: {
borderWidth: 0,
fillOpacity: 0.75
}
});

map.bubbles(${bubblesJson}, {
popupTemplate: function(geo, data) {
return '<div class="hoverinfo">Temperature: ' + data.tavg + '<br/>  Longitude: ' + data.longitude + ' <br/> Latitude: ' + data.latitude + '</div>'
}
});

}, 2000)
</script>