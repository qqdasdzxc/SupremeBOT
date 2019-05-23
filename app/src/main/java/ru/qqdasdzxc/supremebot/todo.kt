package ru.qqdasdzxc.supremebot

//TODO

//НАСТРОЙКИ:

//1) настройка стартовой загружаемой страницы
//возможные выборы - all new jackets shirts tops/sweaters sweatshirts pants shorts hats bags accessories skate
//подсказка - для кроссовок выбрать all или new

//2) настройка названия шмотки
//ввод ключевых слов
//возможно настройку что брать если нужная шмотка солдаут

//3) настройка цвета шмотки
//сделать несколько модов
//3.1)взять конкретный цвет - и выключиться если цвет солдаут
//3.2)взять конкретный цвет - и взять первый попавшийся(или рандом) цвет такой же шмотки если не солдаут
//3.3)взять рандомный цвет

//4) настройка сайза шмотки и сайза кроссовок
//также несколько модов с предпочтительными сайзами и выключениями
//<option value='Small'>Small</option>
//<option value='Medium'>Medium</option>
//<option value='Large'>Large</option>
//<option value='X-Large'>X-Large</option>
//
//<option value='30'>30</option>
//<option value='32'>32</option>
//<option value='34'>34</option>
//<option value='36'>36</option>
//
//<option value='6'>6</option>
//<option value='7'>7</option>
//<option value='8'>8</option>
//<option value='9'>9</option>
//<option value='10'>10</option>
//<option value='11'>11</option>
//<option value='12'>12</option>

//5) настройка адреса и платежа
//тут вроде все просто
//visa = Visa american_express = American Express
//master = Mastercard solo = Solo paypal = Paypal
//card month: 01 - 12
//card year: 2019-2029
//GB = UK //NB = UK(N.IRELAND) //AT = AUSTRIA //BY = BELARUS
//BE = BELGUIM //BG = BULGARIA //HR = CROATIA //CZ = CZECH REBUBLIC //DK = DENMARK
//EE = ESTONIA //FI = FINLAND //FR = FRANCE //DE = GERMANY
//GR = GREECE //HU = HUNGARY //IS = ICELAND //IE = IRELAND //IT = ITALY
//LV = LATVIA //LT = LITHUANIA //LU = LUXEMBOURG //MC = MONACO
//NL = NETHERLANDS //NO = NORWAY //PL = POLAND //PT = PORTUGAL
//RO = ROMANIA //RU = RUSSIA //SK = SLOVAKIA //SI = SLOVENIA
//ES = SPAIN //SE = SWEDEN //CH = SWITZERLAND //TR = TURKEY

//6) настройка периода рефреша страницы в поиске нужной шмотки
//возможно стоит делать дефотную настройку

//Главный экран
//флоу делится на две части:
//возможно надо сделать на главном экране две кнопки - purchase(+ stop working) и test
//1) Тестовая версия:
//будет доступна в бесплатной версии
//в чем смысл: брать рандомную не солдаут шмотку с сайта и проходить до чекаута
//в чекауте заполнять все поля своими, зашитыми значениями,
//оставить что-то пустым и нажимать на кнопку process
//показывая пользователю что все работает

//2) Платная версия:
// кнопка включения постоянного рефреша
// возможно страницу логина по почте и привязке телефона к мылу
// подумать над логином вообщем


//ПОДУМАТЬ
//todo подумать что лучше - цепать сразу все элементы или пройтись по чайлдам вручную
//doc.child(0).child(1).child(2).child(1)
//doc.getElementsByAttributeValueContaining("class", "turbolink_scroller")
//doc.getElementsContainingOwnText("Apple Coaches")
//doc.allElements.firstOrNull { it.attributes().get("class") == "turbolink_scroller" }